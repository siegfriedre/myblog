package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.siegfried.blog.dao.RoleDao;
import com.siegfried.blog.dao.UserAuthDao;
import com.siegfried.blog.dao.UserInfoDao;
import com.siegfried.blog.entity.UserAuth;
import com.siegfried.blog.entity.UserInfo;
import com.siegfried.blog.exception.ServeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.siegfried.blog.constant.RedisPrefixConst.ARTICLE_USER_LIKE;
import static com.siegfried.blog.constant.RedisPrefixConst.COMMENT_USER_LIKE;
import static com.siegfried.blog.utils.UserUtil.convertLoginUser;

/**
 * 处理用户登录
 * @author zy_zhu
 * @date 2021/6/6 10:19
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if(StringUtils.isBlank(username))
            throw new ServeException("用户名不能为空！");
        // 查询账号是否存在
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getId,UserAuth::getUserInfoId, UserAuth::getUsername,UserAuth::getPassword, UserAuth::getLoginType)
                .eq(UserAuth::getUsername,username));
        if(Objects.isNull(user))
            throw new ServeException("用户名不存在!");
        // 查询账号信息
        UserInfo userInfo = userInfoDao.selectOne(new LambdaQueryWrapper<UserInfo>()
                .select(UserInfo::getId, UserInfo::getEmail, UserInfo::getNickname, UserInfo::getAvatar, UserInfo::getIntro, UserInfo::getWebSite, UserInfo::getIsDisable)
                .eq(UserInfo::getId, user.getUserInfoId()));
        // 查询账号角色
        List<String> roleList = roleDao.listRolesByUserInfoId(userInfo.getId());
        // 查询账号点赞信息
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(ARTICLE_USER_LIKE).get(userInfo.getId().toString());
        Set<Integer> commentLikeSet = (Set<Integer>) redisTemplate.boundHashOps(COMMENT_USER_LIKE).get(userInfo.getId().toString());
        // 封装登录信息
        return convertLoginUser(user, userInfo, roleList, articleLikeSet, commentLikeSet, request);
    }
}
