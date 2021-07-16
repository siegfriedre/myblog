package com.siegfried.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.UserInfoDao;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.UserInfoDTO;
import com.siegfried.blog.dto.UserOnlineDTO;
import com.siegfried.blog.entity.UserInfo;
import com.siegfried.blog.entity.UserRole;
import com.siegfried.blog.enums.FilePathEnum;
import com.siegfried.blog.exception.ServeException;
import com.siegfried.blog.service.UserRoleService;
import com.siegfried.blog.service.UserinfoService;
import com.siegfried.blog.utils.OSSUtil;
import com.siegfried.blog.utils.UserUtil;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.EmailVO;
import com.siegfried.blog.vo.UserInfoVO;
import com.siegfried.blog.vo.UserRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.siegfried.blog.constant.RedisPrefixConst.CODE_KEY;

/**
 * Created by zy_zhu on 2021/5/12.
 */
@Service
public class UserinfoServiceImpl extends ServiceImpl<UserInfoDao, UserInfo> implements UserinfoService {

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    private SessionRegistry sessionRegistry;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getLoginUser().getUserInfoId())
                .nickname(userInfoVO.getNickname())
                .intro(userInfoVO.getIntro())
                .webSite(userInfoVO.getWebSite())
                .updateTime(new Date())
                .build();
        userInfoDao.updateById(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateUserAvatar(MultipartFile file) {
        // 头像上传oss，返回图片地址
        String avatar = OSSUtil.upload(file, FilePathEnum.AVATAR.getPath());
        // 更新用户信息
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getLoginUser().getUserInfoId())
                .avatar(avatar)
                .build();
        userInfoDao.updateById(userInfo);
        return avatar;
    }

    @Override
    public void saveUserEmail(EmailVO emailVO) {
        if(!emailVO.getCode().equals(redisTemplate.boundValueOps(CODE_KEY + emailVO.getEmail()).get()))
            throw new ServeException("验证码错误！");
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getLoginUser().getUserInfoId())
                .email(emailVO.getEmail())
                .build();
        userInfoDao.updateById(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserRole(UserRoleVO userRoleVO) {
        // 更新用户角色和昵称
        UserInfo userInfo = UserInfo.builder()
                .id(userRoleVO.getUserInfoId())
                .nickname(userRoleVO.getNickname())
                .build();
        userInfoDao.updateById(userInfo);
        // 删除用户角色重新添加
        userRoleService.remove(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId,userRoleVO.getUserInfoId()));
        List<UserRole> userRoleList = userRoleVO.getRoleIdList().stream()
                .map(roleId -> UserRole.builder()
                        .roleId(roleId)
                        .userId(userRoleVO.getUserInfoId())
                        .build())
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoleList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updaupdateUserDisable(Integer userInfoId, Integer isDisable) {
        UserInfo userInfo = UserInfo.builder()
                .id(userInfoId)
                .isDisable(isDisable)
                .build();
        userInfoDao.updateById(userInfo);
    }

    @Override
    public PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO) {
        // 获取security在线session
        List<UserOnlineDTO> userOnlineDTOList = sessionRegistry.getAllPrincipals().stream()
                .filter(item -> sessionRegistry.getAllSessions(item,false).size() > 0)
                .map(item -> JSON.parseObject(JSON.toJSONString(item),UserOnlineDTO.class))
                .sorted(Comparator.comparing(UserOnlineDTO::getLastLoginTime).reversed())
                .collect(Collectors.toList());
        // 执行分页
        int current = (conditionVO.getCurrent() -1) * conditionVO.getSize();
        int size = userOnlineDTOList.size() > conditionVO.getSize() ? current+conditionVO.getSize() : userOnlineDTOList.size();
        List<UserOnlineDTO> userOnlineList = userOnlineDTOList.subList((conditionVO.getCurrent()-1) * conditionVO.getSize(),size);
        return new PageDTO<>(userOnlineList,userOnlineDTOList.size());
    }

    @Override
    public void removeOnlineUser(Integer userInfoId) {
        // 获取用户session
        List<Object> userInfoList = sessionRegistry.getAllPrincipals().stream().filter(item -> {
            UserInfoDTO userInfoDTO = (UserInfoDTO) item;
            return userInfoDTO.getUserInfoId().equals(userInfoId);
        }).collect(Collectors.toList());
        List<SessionInformation> allSessions = new ArrayList<>();
        userInfoList.forEach(item -> allSessions.addAll(sessionRegistry.getAllSessions(item,false)));
        // 注销session
        allSessions.forEach(SessionInformation::expireNow);
    }
}
