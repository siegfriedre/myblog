package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.ArticleDao;
import com.siegfried.blog.dao.CategoryDao;
import com.siegfried.blog.dto.CategoryDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.entity.Article;
import com.siegfried.blog.entity.Category;
import com.siegfried.blog.service.CategoryService;
import com.siegfried.blog.vo.CategoryVO;
import com.siegfried.blog.vo.ConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siegfried.blog.exception.ServeException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by zy_zhu on 2021/5/2.
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ArticleDao articleDao;

    @Override
    public PageDTO<CategoryDTO> listCategories() {
        return new PageDTO<>(categoryDao.listCategoryDTO(),categoryDao.selectCount(null));
    }

    @Override
    public PageDTO<Category> listCategoryBackDTO(ConditionVO condition) {
        // 分页查询分类列表
        Page<Category> page = new Page<>(condition.getCurrent(),condition.getSize());
        Page<Category> categoryPage = categoryDao.selectPage(page,new LambdaQueryWrapper<Category>()
                .select(Category::getId,Category::getCategoryName,Category::getCreateTime)
                .like(StringUtils.isNotBlank(condition.getKeywords()),Category::getCategoryName,condition.getKeywords())
                .orderByDesc(Category::getId));
        return new PageDTO<>(categoryPage.getRecords(),(int) categoryPage.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        // 判断分类名重复
        Integer count = categoryDao.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getCategoryName,categoryVO.getCategoryName()));
        if(count > 0)
            throw new ServeException("分类名已经存在");
        Category category = Category.builder()
                .id(categoryVO.getId())
                .categoryName(categoryVO.getCategoryName())
                .createTime(Objects.isNull(categoryVO.getId()) ? new Date() : null)
                .build();
        this.saveOrUpdate(category);
    }

    @Override
    public void deleteCategory(List<Integer> categoryIdList) {
        // 查询分类id下是否有文章
        Integer count = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .in(Article::getId,categoryIdList));
        if(count > 0)
            throw new ServeException("删除失败，该分类下存在文章");
        categoryDao.deleteBatchIds(categoryIdList);
    }
}
