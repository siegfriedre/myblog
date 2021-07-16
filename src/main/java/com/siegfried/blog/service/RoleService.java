package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.RoleDTO;
import com.siegfried.blog.dto.UserRoleDTO;
import com.siegfried.blog.entity.Role;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.RoleVO;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/9.
 */
public interface RoleService extends IService<Role> {
    /**
     * 获取用户角色列表
     * @return 用户角色列表
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 角色列表
     * @param conditionVO 条件
     * @return 角色列表
     */
    PageDTO<RoleDTO> listRoles(ConditionVO conditionVO);

    /**
     * 保存或更新角色
     *
     * @param roleVO 角色
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 删除角色
     * @param roleIdList 角色id列表
     */
    void deleteRoles(List<Integer> roleIdList);
}
