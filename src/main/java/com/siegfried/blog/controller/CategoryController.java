package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.ArticlePreviewListDTO;
import com.siegfried.blog.dto.CategoryDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.entity.Category;
import com.siegfried.blog.service.ArticleService;
import com.siegfried.blog.service.CategoryService;
import com.siegfried.blog.vo.CategoryVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.locks.Condition;

import static com.siegfried.blog.constant.OptTypeConst.REMOVE;
import static com.siegfried.blog.constant.OptTypeConst.SAVE_OR_UPDATE;

/**
 * Created by zy_zhu on 2021/5/2.
 */
@Api(tags = "categories")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看分类列表")
    @GetMapping("/categories")
    public Result<PageDTO<CategoryDTO>> listCategory(){
        return new Result<>(true, StatusConst.OK,"查询成功",categoryService.listCategories());
    }

    @ApiOperation(value = "查看某个分类下的文章列表")
    @GetMapping("/categories/{categoryId}")
    public Result<ArticlePreviewListDTO> listArticleByCategoryId(@PathVariable("categoryId") Integer categoryId, Integer current){
        ConditionVO condition = ConditionVO.builder()
                .categoryId(categoryId)
                .current(current)
                .build();
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listArticlesByCondition(condition));
    }


    //------------后台接口-----------------

    @ApiOperation(value = "查看后台分类列表")
    @GetMapping("/admin/categories")
    public Result<PageDTO<Category>> listCategoryBackDTO(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",categoryService.listCategoryBackDTO(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "添加或修改分类")
    @PostMapping("/admin/categories")
    public Result saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO){
        categoryService.saveOrUpdateCategory(categoryVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除分类")
    @DeleteMapping("/admin/categories")
    public Result deleteCategory(@RequestBody List<Integer> categoryIdList){
        categoryService.deleteCategory(categoryIdList);
        return new Result(true,StatusConst.OK,"删除成功");
    }
}
