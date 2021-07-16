package com.siegfried.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.constant.CommonConst;
import com.siegfried.blog.dao.UserAuthDao;
import com.siegfried.blog.dao.UserInfoDao;
import com.siegfried.blog.dao.UserRoleDao;
import com.siegfried.blog.dto.EmailDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.UserBackDTO;
import com.siegfried.blog.vo.ConditionVO;
import org.springframework.amqp.core.Message;
import com.siegfried.blog.entity.UserAuth;
import com.siegfried.blog.entity.UserInfo;
import com.siegfried.blog.entity.UserRole;
import com.siegfried.blog.enums.LoginTypeEnum;
import com.siegfried.blog.enums.RoleEnum;
import com.siegfried.blog.exception.ServeException;
import com.siegfried.blog.service.UserAuthService;
import com.siegfried.blog.utils.UserUtil;
import com.siegfried.blog.vo.PasswordVO;
import com.siegfried.blog.vo.UserVO;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.siegfried.blog.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.siegfried.blog.constant.RedisPrefixConst.CODE_EXPIRE_TIME;
import static com.siegfried.blog.constant.RedisPrefixConst.CODE_KEY;
import static com.siegfried.blog.utils.CommonUtil.checkEmail;

/**
 * @author zy_zhu
 * @date 2021/5/23 14:54
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthDao, UserAuth> implements UserAuthService {

    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUser(UserVO user) {
        // 校验账号是否合法
        if(!checkUser(user))
            throw new ServeException("邮箱已被注册");
        // 新增用户信息
        UserInfo userInfo = UserInfo.builder()
                .email(user.getUsername())
                .nickname(CommonConst.DEFAULT_NICKNAME)
                .avatar(CommonConst.DEFAULT_AVATAR)
                .createTime(new Date())
                .build();
        userInfoDao.insert(userInfo);
        // 绑定用户角色
        saveUserRole(userInfo);
        // 新增用户账号
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()))
                .createTime(new Date())
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthDao.insert(userAuth);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassword(UserVO user) {
        // 校验账号是否合法
        if(!checkUser(user))
            throw new ServeException("邮箱尚未注册！");
        // 根据用户名修改密码
        userAuthDao.update(new UserAuth(),new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword,BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()))
                .eq(UserAuth::getUsername,user.getUsername()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // 查询旧密码是否正确
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, UserUtil.getLoginUser().getId()));
        // 正确则修改密码，错误则提示不正确
        if(Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(),user.getPassword())){
            UserAuth userAuth = UserAuth.builder()
                    .id(UserUtil.getLoginUser().getId())
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(),BCrypt.gensalt()))
                    .build();
            userAuthDao.updateById(userAuth);
        }
        else
            throw new ServeException("旧密码不正确");
    }

    @Override
    public void sendCode(String username) {
        // 校验账号是否合法
        if(!checkEmail(username)){
            throw new ServeException("请输入正确邮箱");
        }
        // 生成六位随机验证码发送
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for(int i = 0;i<6;++i)
            code.append(random.nextInt(10));
        // 发送验证码
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .content("您的验证码为 " + code.toString() + " 有效期15分钟，请勿告诉他人")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE,"*",new Message(JSON.toJSONBytes(emailDTO),new MessageProperties()));
        // 将验证码存入redis，设置过期时间为15分钟
        redisTemplate.boundValueOps(CODE_KEY + username).set(code);
        redisTemplate.expire(CODE_KEY + username,CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        // 转换页码
        condition.setCurrent((condition.getCurrent()-1) * condition.getSize());
        // 获取后台用户数量
        Integer count = userAuthDao.countUser(condition);
        if(count == 0)
            return new PageDTO<>();
        // 获取后台用户列表
        List<UserBackDTO> userBackDTOList = userAuthDao.listUsers(condition);
        return new PageDTO<>(userBackDTOList,count);
    }


//--------------------工具函数--------------------------------
    /**
     * 绑定用户角色
     * @param userInfo 用户信息
     */
    private  void saveUserRole(UserInfo userInfo){
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleDao.insert(userRole);
    }

    /**
     * 校验用户数据是否合法
     * @param user 用户VO
     * @return 是否合法
     */
    public Boolean checkUser(UserVO user){
        if(!user.getCode().equals(redisTemplate.boundValueOps(CODE_KEY + user.getUsername()).get())){
            throw new ServeException("验证码错误！");
        }
        //查询用户名是否存在
        UserAuth userAuth = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername).eq(UserAuth::getUsername,user.getUsername()));
        return Objects.nonNull(userAuth);
    }
}
