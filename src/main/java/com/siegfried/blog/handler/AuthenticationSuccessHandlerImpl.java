package com.siegfried.blog.handler;

import com.alibaba.fastjson.JSON;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dao.UserAuthDao;
import com.siegfried.blog.dto.UserInfoDTO;
import com.siegfried.blog.dto.UserLoginDTO;
import com.siegfried.blog.entity.UserAuth;
import com.siegfried.blog.utils.BeanCopyUtil;
import com.siegfried.blog.utils.UserUtil;
import com.siegfried.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理
 *
 * @author zy_zhu
 * @date 2021/6/5 14:51
 */
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private UserAuthDao userAuthDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        // 更新用户ip，最近登录时间
        updateUserInfo();
        UserLoginDTO userLoginDTO = BeanCopyUtil.copyObject(UserUtil.getLoginUser(), UserLoginDTO.class);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<UserInfoDTO>(true, StatusConst.OK, "登录成功！", userLoginDTO)));
    }

    /**
     * 更新用户信息
     */
    @Async
    public void updateUserInfo() {
        UserAuth userAuth = UserAuth.builder()
                .id(UserUtil.getLoginUser().getId())
                .ipAddr(UserUtil.getLoginUser().getIpAddr())
                .ipSource(UserUtil.getLoginUser().getIpSource())
                .lastLoginTime(UserUtil.getLoginUser().getLastLoginTime())
                .build();
        userAuthDao.updateById(userAuth);
    }
}
