package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.MenuDao;
import com.siegfried.blog.dto.LabelOptionDTO;
import com.siegfried.blog.dto.MenuDTO;
import com.siegfried.blog.dto.UserMenuDTO;
import com.siegfried.blog.entity.Menu;
import com.siegfried.blog.service.MenuService;
import com.siegfried.blog.utils.BeanCopyUtil;
import com.siegfried.blog.utils.UserUtil;
import com.siegfried.blog.vo.ConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.siegfried.blog.constant.CommonConst.*;

/**
 * @author zy_zhu
 * @date 2021/5/24 16:11
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Override
    public List<MenuDTO> listMenus(ConditionVO conditionVO) {
        // 查询菜单数据
        List<Menu> menuList = this.list(new LambdaQueryWrapper<Menu>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),Menu::getName,conditionVO.getKeywords()));
        // 获取目录列表
        List<Menu> catalogList = listCatalog(menuList);
        // 获取目录下的子菜单
        Map<Integer,List<Menu>> childrenMap = getMenuMap(menuList);
        // 组装目录菜单数据
        return catalogList.stream().map(item -> {
            MenuDTO menuDTO = BeanCopyUtil.copyObject(item,MenuDTO.class);
            // 获取目录下的菜单排序
            List<MenuDTO> list = BeanCopyUtil.copyList(childrenMap.get(item.getId()),MenuDTO.class).stream()
                    .sorted(Comparator.comparing(MenuDTO::getOrderNum))
                    .collect(Collectors.toList());
            menuDTO.setChildren(list);
            return menuDTO;
        }).sorted(Comparator.comparing(MenuDTO::getOrderNum)).collect(Collectors.toList());
    }

    @Override
    public List<LabelOptionDTO> listMenuOptions() {
        // 查询菜单数据
        List<Menu> menuList = this.list(new LambdaQueryWrapper<Menu>()
                .select(Menu::getId,Menu::getName,Menu::getParentId,Menu::getOrderNum));
        // 获取目录列表
        List<Menu> catalogList = listCatalog(menuList);
        // 获取目录下的子菜单
        Map<Integer, List<Menu>> childrenMap = getMenuMap(menuList);
        // 组装目录菜单数据
        return catalogList.stream().map(item -> {
            // 获取目录下的菜单排序
            List<LabelOptionDTO> list = new ArrayList<>();
            List<Menu> children = childrenMap.get(item.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                list = children.stream()
                        .sorted(Comparator.comparing(Menu::getOrderNum))
                        .map(menu -> LabelOptionDTO.builder()
                                .id(menu.getId())
                                .label(menu.getName())
                                .build())
                        .collect(Collectors.toList());
            }
            return LabelOptionDTO.builder()
                    .id(item.getId())
                    .label(item.getName())
                    .children(list)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserMenuDTO> listUserMenus() {
        // 查询用户菜单信息
        List<Menu> menuList = menuDao.listMenusByUserInfoId(UserUtil.getLoginUser().getUserInfoId());
        // 获取目录列表
        List<Menu> catalogList = listCatalog(menuList);
        // 获取目录下的子菜单
        Map<Integer, List<Menu>> childrenMap = getMenuMap(menuList);
        // 转换前端菜单格式
        return convertUserMenuList(catalogList, childrenMap);
    }


    //-----------------------工具函数----------------------------
    /**
     * 获取目录列表
     *
     * @param menuList 菜单列表
     * @return 目录列表
     */
    private List<Menu> listCatalog(List<Menu> menuList){
        return menuList.stream()
                .filter(item -> Objects.isNull(item.getParentId()))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 获取目录下菜单列表
     *
     * @param menuList 菜单列表
     * @return 目录下的菜单列表
     */
    private Map<Integer,List<Menu>> getMenuMap(List<Menu> menuList){
        return menuList.stream()
                .filter(item -> Objects.nonNull(item.getParentId()))
                .collect(Collectors.groupingBy(Menu::getParentId));
    }

    /**
     * 转换用户菜单格式
     *
     * @param catalogList 目录
     * @param childrenMap 子菜单
     */
    private List<UserMenuDTO> convertUserMenuList(List<Menu> catalogList, Map<Integer, List<Menu>> childrenMap) {
        return catalogList.stream().map(item -> {
            // 获取目录
            UserMenuDTO userMenuDTO = new UserMenuDTO();
            List<UserMenuDTO> list = new ArrayList<>();
            // 获取目录下的子菜单
            List<Menu> children = childrenMap.get(item.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                // 多级菜单处理
                userMenuDTO = BeanCopyUtil.copyObject(item, UserMenuDTO.class);
                list = children.stream()
                        .sorted(Comparator.comparing(Menu::getOrderNum))
                        .map(menu -> {
                            UserMenuDTO dto = BeanCopyUtil.copyObject(menu, UserMenuDTO.class);
                            dto.setHidden(menu.getIsHidden().equals(TRUE));
                            return dto;
                        }).collect(Collectors.toList());
            } else {
                // 一级菜单处理
                userMenuDTO.setPath(item.getPath());
                userMenuDTO.setComponent(COMPONENT);
                list.add(UserMenuDTO.builder()
                        .path("")
                        .name(item.getName())
                        .icon(item.getIcon())
                        .component(item.getComponent())
                        .build());
            }
            userMenuDTO.setHidden(item.getIsHidden().equals(TRUE));
            userMenuDTO.setChildren(list);
            return userMenuDTO;
        }).collect(Collectors.toList());
    }
}
