package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.dto.*;
import com.siegfried.blog.entity.Article;
import com.siegfried.blog.entity.ArticleTag;
import com.siegfried.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 访问数据库，crud等等，具体实现用spring自动创建的实体类来操作
 * Created by zy_zhu on 2021/4/21.
 */
@Repository    //@MapperScan扫描持久层接口创建实现类并交给spring管理，不怕麻烦用@Mapper也行
public interface ArticleDao extends BaseMapper<Article> {
    /**
     * 查询首页文章
     *
     * @param current 当前页码
     * @return 首页文章列表
     */
    List<ArticleHomeDTO> listArticles(Long current);

    /**
     * 根据条件查询文章
     * @Param 注解的作用是给参数命名,参数命名后就能根据名字得到参数值,正确的将参数传入sql语句中
     * @param condition 条件
     * @return 文章集合
     */
    List<ArticlePreviewDTO> listArticlesByCondition(@Param("condition") ConditionVO condition);

    /**
     * 根据id查询文章
     * @param articleId 文章id
     * @return 文章DTO
     */
    ArticleDTO getArticleById(Integer articleId);

    /**
     * 根据id查询推荐文章列表
     * @param articleId 文章id
     * @return 推荐文章列表
     */
    List<ArticleRecommendDTO> listArticleRecommends(Integer articleId);

    /**
     * 查询文章排行
     * @param articleIdList
     * @return
     */
    List<Article> listArticleRank(@Param("articleIdList") List<Integer> articleIdList);

    /**
     * 查询后台文章总量
     * @param condition 条件
     * @return 文章总量
     */
    Integer countArticleBacks(@Param("condition") ConditionVO condition);

    /**
     * 查询后台文章
     * @param condition 条件
     * @return 后台文章集合
     */
    List<ArticleBackDTO> listArticleBacks(@Param("condition") ConditionVO condition);
}
