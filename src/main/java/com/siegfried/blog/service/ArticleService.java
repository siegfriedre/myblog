package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.*;
import com.siegfried.blog.entity.Article;
import com.siegfried.blog.vo.ArticleVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.DeleteVO;
import com.siegfried.blog.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by zy_zhu on 2021/4/21.
 */
public interface ArticleService extends IService<Article> {

    /**
     * 查询文章归档
     * @param current 当前页码
     * @return 文章
     */
    PageDTO<ArchiveDTO> listArchives(Long current);

    /**
     * 查询首页文章
     *
     * @param current 当前页码
     * @return 文章
     */
    List<ArticleHomeDTO> listArticles(Long current);

    /**
     * 根据条件查询文章列表
     * @param conditionVO 条件
     * @return 文章
     */
    ArticlePreviewListDTO listArticlesByCondition(ConditionVO conditionVO);

    /**
     * 根据id查询文章
     * @param articleId
     * @return
     */
    ArticleDTO getArticleById(Integer articleId);

    /**
     * 根据id点赞文章，使redis的点赞数++
     * @param articleId 文章id
     * @return 1 or 0
     */
    int saveArticleLike(Integer articleId);

    /**
     * 查看最新文章
     * @return  最新文章列表
     */
    List<ArticleRecommendDTO> listNewestArticles();

    /**
     * 新增或修改文章
     * @param articleVO 文章VO
     */
    void saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 根据条件查询后台文章列表，有部分展示内容（分类，置顶等）没封装在这里，单独封装
     * @param conditionVO 条件
     * @return 文章列表的pageDTO
     */
    PageDTO<ArticleBackDTO> listArticleBackDTO(ConditionVO conditionVO);

    /**
     * 根据id查看后台文章
     * @param articleId 文章id
     * @return 文章
     */
    ArticleVO getArticleBackById(Integer articleId);

    /**
     * 查看文章的分类和标签，并展示在后台文章列表
     * @return
     */
    ArticleOptionDTO listArticleOptionDTO();

    /**
     * 修改文章置顶，同样展示在后台文章列表那里
     * @param articleId 文章id
     * @param isTop 置顶状态
     */
    void updateArticleTop(Integer articleId, Integer isTop);

    /**
     * 删除或恢复文章
     * @param deleteVO 逻辑删除对象
     */
    void updateArticleDelete(DeleteVO deleteVO);

    /**
     * 物理删除文章
     * @param articleIdList 文章id列表
     */
    void deleteArticles(List<Integer> articleIdList);

    /**
     * 搜索文章
     * @param condition 搜索条件
     * @return
     */
    List<ArticleSearchDTO> listArticleBySearch(ConditionVO condition);
}
