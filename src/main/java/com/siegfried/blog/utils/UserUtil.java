package com.siegfried.blog.utils;

import com.siegfried.blog.dto.UserInfoDTO;
import com.siegfried.blog.entity.UserAuth;
import com.siegfried.blog.entity.UserInfo;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户工具类
 * Created by zy_zhu on 2021/5/4.
 */
public class UserUtil {
    /**
     * 获取当前登录用户
     * @return 用户登录信息
     */
    public static UserInfoDTO getLoginUser(){
        return (UserInfoDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static UserInfoDTO convertLoginUser(UserAuth user, UserInfo userInfo, List<String> roleList, Set<Integer> articleLikeSet, Set<Integer> commentLikeSet, HttpServletRequest request){
        // 获取登录信息
        String ipAddr = IpUtil.getIpAddr(request);
        String ipSource = IpUtil.getIpSource(ipAddr);
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        // 封装权限集合
        return UserInfoDTO.builder()
                .id(user.getId())
                .loginType(user.getLoginType())
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(userInfo.getEmail())
                .roleList(roleList)
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .webSite(userInfo.getWebSite())
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .ipAddr(ipAddr)
                .ipSource(ipSource)
                .browser(userAgent.getBrowser().getName())
                .os(userAgent.getOperatingSystem().getName())
                .lastLoginTime(new Date())
                .build();
    }
}
