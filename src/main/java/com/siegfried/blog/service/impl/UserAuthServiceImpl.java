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
        // ????????????????????????
        if(!checkUser(user))
            throw new ServeException("??????????????????");
        // ??????????????????
        UserInfo userInfo = UserInfo.builder()
                .email(user.getUsername())
                .nickname(CommonConst.DEFAULT_NICKNAME)
                .avatar(CommonConst.DEFAULT_AVATAR)
                .createTime(new Date())
                .build();
        userInfoDao.insert(userInfo);
        // ??????????????????
        saveUserRole(userInfo);
        // ??????????????????
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
        // ????????????????????????
        if(!checkUser(user))
            throw new ServeException("?????????????????????");
        // ???????????????????????????
        userAuthDao.update(new UserAuth(),new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword,BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()))
                .eq(UserAuth::getUsername,user.getUsername()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // ???????????????????????????
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, UserUtil.getLoginUser().getId()));
        // ????????????????????????????????????????????????
        if(Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(),user.getPassword())){
            UserAuth userAuth = UserAuth.builder()
                    .id(UserUtil.getLoginUser().getId())
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(),BCrypt.gensalt()))
                    .build();
            userAuthDao.updateById(userAuth);
        }
        else
            throw new ServeException("??????????????????");
    }

    @Override
    public void sendCode(String username) {
        // ????????????????????????
        if(!checkEmail(username)){
            throw new ServeException("?????????????????????");
        }
        // ?????????????????????????????????
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for(int i = 0;i<6;++i)
            code.append(random.nextInt(10));
        // ???????????????
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("?????????")
                .content("?????????????????? " + code.toString() + " ?????????15???????????????????????????")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE,"*",new Message(JSON.toJSONBytes(emailDTO),new MessageProperties()));
        // ??????????????????redis????????????????????????15??????
        redisTemplate.boundValueOps(CODE_KEY + username).set(code);
        redisTemplate.expire(CODE_KEY + username,CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        // ????????????
        condition.setCurrent((condition.getCurrent()-1) * condition.getSize());
        // ????????????????????????
        Integer count = userAuthDao.countUser(condition);
        if(count == 0)
            return new PageDTO<>();
        // ????????????????????????
        List<UserBackDTO> userBackDTOList = userAuthDao.listUsers(condition);
        return new PageDTO<>(userBackDTOList,count);
    }


//--------------------????????????--------------------------------
    /**
     * ??????????????????
     * @param userInfo ????????????
     */
    private  void saveUserRole(UserInfo userInfo){
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleDao.insert(userRole);
    }

    /**
     * ??????????????????????????????
     * @param user ??????VO
     * @return ????????????
     */
    public Boolean checkUser(UserVO user){
        if(!user.getCode().equals(redisTemplate.boundValueOps(CODE_KEY + user.getUsername()).get())){
            throw new ServeException("??????????????????");
        }
        //???????????????????????????
        UserAuth userAuth = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername).eq(UserAuth::getUsername,user.getUsername()));
        return Objects.nonNull(userAuth);
    }
}
