package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.UserOnlineDTO;
import com.siegfried.blog.entity.UserInfo;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.EmailVO;
import com.siegfried.blog.vo.UserInfoVO;
import com.siegfried.blog.vo.UserRoleVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zy_zhu on 2021/5/12.
 */
public interface UserinfoService extends IService<UserInfo> {

    /**
     * 修改用户资料
     * @param userInfoVO 用户资料
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 修改用户头像
     * @param file 头像图片
     * @return 头像OSS地址
     */
    String updateUserAvatar(MultipartFile file);

    /**
     * 绑定用户邮箱
     * @param emailVO 邮箱
     */
    void saveUserEmail(EmailVO emailVO);

    /**
     * 修改用户角色
     * @param userRoleVO 用户角色
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 修改用户禁用状态
     * @param userInfoId 用户信息id
     * @param isDisable 禁用状态
     */
    void updaupdateUserDisable(Integer userInfoId,Integer isDisable);

    /**
     * 查看在线用户列表
     * @param conditionVO 条件
     * @return 在线用户列表
     */
    PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO);

    /**
     * 下线用户
     * @param userInfoId 用户信息id
     */
    void removeOnlineUser(Integer userInfoId);
}
