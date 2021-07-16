package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.*;
import com.siegfried.blog.enums.FilePathEnum;
import com.siegfried.blog.service.ArticleService;
import com.siegfried.blog.utils.OSSUtil;
import com.siegfried.blog.vo.ArticleVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.DeleteVO;
import com.siegfried.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.siegfried.blog.constant.OptTypeConst.REMOVE;
import static com.siegfried.blog.constant.OptTypeConst.UPDATE;

/**
 * Created by zy_zhu on 2021/4/21.
 */
@Api(tags = "article")
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看文章归档")
    @ApiImplicitParam(name = "current",value = "当前页码", required = true,dataType = "Long")
    @GetMapping("/articles/archives")
    public Result<PageDTO<ArchiveDTO>> listArchives(Long current){
        return new Result<>(true, StatusConst.OK,"查询成功",articleService.listArchives(current));
    }

    @ApiOperation(value = "查看首页文章")
    @ApiImplicitParam(name = "current",value = "当前页码", required = true,dataType = "Long")
    @GetMapping("/articles")
    public Result<ArticleHomeDTO> listArticles(Long current){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listArticles(current));
    }

    @ApiOperation("根据id查看文章")
    @GetMapping("/articles/{articleId}")
    public Result<ArticleDTO> getArticleById(@PathVariable("articleId") Integer articleId){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.getArticleById(articleId));
    }

    @ApiOperation("点赞文章")
    @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer")
    @PostMapping("/articles/like")
    public Result saveArticleLike(Integer articleId){
        articleService.saveArticleLike(articleId);
        return new Result(true,StatusConst.OK,"点赞成功");
    }

    @ApiOperation("最新文章列表")
    @GetMapping("/articles/newest")
    public Result<List<ArticleRecommendDTO>> listNewestArticles(){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listNewestArticles());
    }

    @ApiOperation(value = "搜索文章")
    @GetMapping("/articles/search")
    public Result<List<ArticleSearchDTO>> listArticleBySearch(ConditionVO condition){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listArticleBySearch(condition));
    }

    //------------后台接口-----------------

    @OptLog(optType = REMOVE)
    @ApiOperation("添加或修改文章")
    @PostMapping("/admin/articles")
    public Result saveArticle(@Valid @RequestBody ArticleVO articleVO){
        articleService.saveOrUpdateArticle(articleVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @ApiOperation("查看后台文章")
    @GetMapping("/admin/articles")
    public Result<PageDTO<ArticleBackDTO>> listArticleBackDTO(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listArticleBackDTO(conditionVO));
    }

    @ApiOperation("根据id查看后台文章")
    @GetMapping("/admin/articles/{articleId}")
    public Result<ArticleVO> getArticleBackById(@PathVariable("articleId") Integer articleId){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.getArticleBackById(articleId));
    }

    @ApiOperation("查看文章选项")
    @GetMapping("/admin/articles/options")
    public Result<ArticleOptionDTO> listArticleOptionDTO(){
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listArticleOptionDTO());
    }

    @OptLog(optType = UPDATE)
    @ApiOperation("修改文章置顶")
    @PutMapping("/admin/articles/top/{articleId}")
    public Result updateArticleTop(@PathVariable("articleId") Integer articleId,Integer isTop){
        articleService.updateArticleTop(articleId,isTop);
        return new Result(true,StatusConst.OK,"修改成功");
    }

    @ApiOperation(value = "上传文章图片")
    @ApiImplicitParam(name = "file", value = "文章图片", required = true, dataType = "MultipartFile")
    @PostMapping("/admin/articles/images")
    public Result<String> saveArticleImages(MultipartFile file){
        return new Result<>(true,StatusConst.OK,"上传成功", OSSUtil.upload(file, FilePathEnum.ARTICLE.getPath()));
    }

    @OptLog(optType = UPDATE)
    @ApiOperation("恢复或删除文章")
    @PutMapping("/admin/articles")
    public Result updateArticleDelete(DeleteVO deleteVO){
        articleService.updateArticleDelete(deleteVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "物理删除文章")
    @DeleteMapping("/admin/articles")
    public Result deleteArticles(@RequestBody List<Integer> articleIdList){
        articleService.deleteArticles(articleIdList);
        return new Result(true,StatusConst.OK,"删除成功");
    }
}
