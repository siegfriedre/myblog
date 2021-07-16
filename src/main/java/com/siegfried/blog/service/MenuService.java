package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.LabelOptionDTO;
import com.siegfried.blog.dto.MenuDTO;
import com.siegfried.blog.dto.UserMenuDTO;
import com.siegfried.blog.entity.Menu;
import com.siegfried.blog.vo.ConditionVO;

import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/5/24 16:05
 */
public interface MenuService extends IService<Menu> {

    /**
     * 查看菜单列表
     * @param conditionVO 条件
     * @return 菜单列表
     */
    List<MenuDTO> listMenus(ConditionVO conditionVO);

    /**
     * 查看角色菜单选项
     * @return 角色菜单选项
     */
    List<LabelOptionDTO> listMenuOptions();

    /**
     * 查看用户菜单
     * @return 菜单列表
     */
    List<UserMenuDTO> listUserMenus();
}
