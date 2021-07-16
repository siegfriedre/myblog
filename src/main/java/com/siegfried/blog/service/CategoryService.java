package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.CategoryDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.entity.Category;
import com.siegfried.blog.vo.CategoryVO;
import com.siegfried.blog.vo.ConditionVO;

import java.util.List;


/**
 * Created by zy_zhu on 2021/5/2.
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询分类列表
     * @return 分类列表
     */
    PageDTO<CategoryDTO> listCategories();

    /**
     * 根据条件查询后端分类的分页列表
     * @param condition 条件
     * @return 后端分类的分页列表
     */
    PageDTO<Category> listCategoryBackDTO(ConditionVO condition);

    /**
     * 添加或修改分类
     * @param categoryVO 分类
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

    /**
     * 根据id列表删除分类
     * @param categoryIdList id列表
     */
    void deleteCategory(List<Integer> categoryIdList);
}
