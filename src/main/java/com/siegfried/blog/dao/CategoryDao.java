package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.dto.CategoryDTO;
import com.siegfried.blog.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/2.
 */
@Repository
public interface CategoryDao extends BaseMapper<Category> {
    /**
     * 查询分类列表和对应文章数量
     * @return 分类列表
     */
    List<CategoryDTO> listCategoryDTO();
}
