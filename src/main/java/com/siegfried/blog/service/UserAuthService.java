package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.UserBackDTO;
import com.siegfried.blog.entity.UserAuth;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.PasswordVO;
import com.siegfried.blog.vo.UserVO;

/**
 * @author zy_zhu
 * @date 2021/5/23 14:49
 */
public interface UserAuthService extends IService<UserAuth> {

    /**
     * 用户注册
     * @param user 用户VO
     */
    void saveUser(UserVO user);

    /**
     * 修改密码
     * @param user 用户对象
     */
    void updatePassword(UserVO user);

    /**
     * 修改管理员密码
     * @param passwordVO 密码对象
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 发送邮箱验证码
     *
     * @param username 邮箱号
     */
    void sendCode(String username);

    /**
     *
     * @param condition 条件
     * @return 用户列表
     */
    PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition);
}
