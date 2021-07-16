package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.entity.Menu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/5/24 16:12
 */
@Repository
public interface MenuDao extends BaseMapper<Menu> {

    /**
     * 根据用户id查询菜单
     * @param userInfoId 用户信息id
     * @return 菜单列表
     */
    List<Menu> listMenusByUserInfoId(Integer userInfoId);
}
