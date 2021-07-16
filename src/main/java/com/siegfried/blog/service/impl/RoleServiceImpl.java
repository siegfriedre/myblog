package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.constant.CommonConst;
import com.siegfried.blog.dao.RoleDao;
import com.siegfried.blog.dao.UserRoleDao;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.RoleDTO;
import com.siegfried.blog.dto.UserRoleDTO;
import com.siegfried.blog.entity.Role;
import com.siegfried.blog.entity.RoleMenu;
import com.siegfried.blog.entity.UserRole;
import com.siegfried.blog.exception.ServeException;
import com.siegfried.blog.service.RoleMenuService;
import com.siegfried.blog.service.RoleService;
import com.siegfried.blog.utils.BeanCopyUtil;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by zy_zhu on 2021/5/9.
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

    @Autowired
    private RoleDao roleDao;
//    @Autowired
//    private RoleResourceService roleResourceService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserRoleDao userRoleDao;
//    @Autowired
//    private FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;

    @Override
    public List<UserRoleDTO> listUserRoles() {
        // 查询角色列表
        List<Role> roleList = roleDao.selectList(new LambdaQueryWrapper<Role>()
                .select(Role::getId,Role::getRoleName));
        return BeanCopyUtil.copyList(roleList,UserRoleDTO.class);
    }

    @Override
    public PageDTO<RoleDTO> listRoles(ConditionVO conditionVO) {
        // 转换页码
        conditionVO.setCurrent((conditionVO.getCurrent()-1) * conditionVO.getSize());
        // 查询角色列表
        List<RoleDTO> roleDTOList = roleDao.listRoles(conditionVO);
        // 查询总量
        Integer count = roleDao.selectCount(new LambdaQueryWrapper<Role>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()) , Role::getRoleName,conditionVO.getKeywords()));
        return new PageDTO<>(roleDTOList,count);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateRole(RoleVO roleVO) {
        // 判断角色名重复
        Integer count = roleDao.selectCount(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleName,roleVO.getRoleName()));
        if(Objects.isNull(roleVO.getId()) && count > 0)
            throw new ServeException("角色名已存在");
        // 保存或更新角色信息
        Role role = Role.builder()
                .id(roleVO.getId())
                .roleName(roleVO.getRoleName())
                .roleLabel(roleVO.getRoleLabel())
                .createTime(Objects.isNull(roleVO.getId()) ? new Date() : null)
                .updateTime(Objects.nonNull(roleVO.getId()) ? new Date() : null)
                .isDisable(CommonConst.FALSE)
                .build();
        this.saveOrUpdate(role);
        // 更新资源列表
    /*    if (CollectionUtils.isNotEmpty(roleVO.getResourceIdList())) {
            if (Objects.nonNull(roleVO.getId())) {
                roleResourceService.remove(new LambdaQueryWrapper<RoleResource>().eq(RoleResource::getRoleId, roleVO.getId()));
            }
            List<RoleResource> roleResourceList = roleVO.getResourceIdList().stream()
                    .map(resourceId -> RoleResource.builder()
                            .roleId(role.getId())
                            .resourceId(resourceId)
                            .build())
                    .collect(Collectors.toList());
            roleResourceService.saveBatch(roleResourceList);
            // 重新加载角色资源信息
            filterInvocationSecurityMetadataSource.clearDataSource();
        }*/
        // 更新菜单列表
        if(CollectionUtils.isNotEmpty(roleVO.getMenuIdList())){
            if(Objects.nonNull(roleVO.getId())){
                roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId,roleVO.getId()));
            }
            List<RoleMenu> roleMenuList = roleVO.getMenuIdList().stream()
                    .map(menuId -> RoleMenu.builder()
                            .roleId(role.getId())
                            .menuId(menuId)
                            .build())
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenuList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRoles(List<Integer> roleIdList) {
        // 判断角色下是否有用户
        Integer count = userRoleDao.selectCount(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId,roleIdList));
        if(count > 0)
            throw new ServeException("该角色下存在用户");
        roleDao.deleteBatchIds(roleIdList);
    }
}
