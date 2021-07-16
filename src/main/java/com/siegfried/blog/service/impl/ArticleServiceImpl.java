package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.ArticleDao;
import com.siegfried.blog.dao.ArticleTagDao;
import com.siegfried.blog.dao.CategoryDao;
import com.siegfried.blog.dao.TagDao;
import com.siegfried.blog.dto.*;
import com.siegfried.blog.entity.Article;
import com.siegfried.blog.entity.ArticleTag;
import com.siegfried.blog.entity.Category;
import com.siegfried.blog.entity.Tag;
import com.siegfried.blog.service.ArticleService;
import com.siegfried.blog.service.ArticleTagService;
import com.siegfried.blog.utils.BeanCopyUtil;
import com.siegfried.blog.utils.RandomCoverUtil;
import com.siegfried.blog.utils.UserUtil;
import com.siegfried.blog.vo.ArticleVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.DeleteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;

import static com.siegfried.blog.constant.CommonConst.FALSE;
import static com.siegfried.blog.constant.RedisPrefixConst.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 加上@Service自动注入容器供controller调用，它通过调用dao来实现访问数据库，返回数据给controller
 *
 * Created by zy_zhu on 2021/4/21.
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private HttpSession session;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleTagDao articleTagDao;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private RandomCoverUtil randomCoverUtil;

    @Override
    public PageDTO<ArchiveDTO> listArchives(Long current){
        //获取分页数据
        Page<Article> page = new Page<>(current,10);
        Page<Article> articlePage = articleDao
                .selectPage(page,new LambdaQueryWrapper<Article>()
                .select(Article::getId,Article::getArticleTitle,Article::getCreateTime)
                .orderByDesc(Article::getCreateTime)
                .eq(Article::getIsDelete,FALSE)
                .eq(Article::getIsDraft,FALSE));
        //拷贝DTO集合
        List<ArchiveDTO> archiveDTOList = BeanCopyUtil.copyList(articlePage.getRecords(), ArchiveDTO.class);
        return new PageDTO<>(archiveDTOList,(int)articlePage.getTotal());
    }

    @Override
    public List<ArticleHomeDTO> listArticles(Long current){
        List<ArticleHomeDTO> articleHomeDTOList = articleDao.listArticles((current-1)*10);
        return articleHomeDTOList;
    }

    @Override
    public ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition) {
        //转换页码
        condition.setCurrent((condition.getCurrent()-1)*9);
        //搜索条件对应数据
        List<ArticlePreviewDTO> articlePreviewDTOList = articleDao.listArticlesByCondition(condition);
        String name;
        if(Objects.nonNull(condition.getCategoryId())){
            name = categoryDao.selectOne(new LambdaQueryWrapper<Category>()
                    .select(Category::getCategoryName)
                    .eq(Category::getId,condition.getCategoryId()))
                    .getCategoryName();
        }
        else{
            name = tagDao.selectOne(new LambdaQueryWrapper<Tag>()
                    .select(Tag::getTagName)
                    .eq(Tag::getId,condition.getTagId()))
                    .getTagName();
        }
        return ArticlePreviewListDTO.builder()
                .articlePreviewDTOList(articlePreviewDTOList)
                .name(name)
                .build();
    }

    @Override
    public ArticleDTO getArticleById(Integer articleId) {
        // 更新文章浏览量
        updateArticleViewsCount(articleId);
        // 查询id对应的文章
        ArticleDTO article = articleDao.getArticleById(articleId);
        // 查询上一篇下一篇文章
        Article lastArticle = articleDao.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId,Article::getArticleTitle,Article::getArticleCover)
                .eq(Article::getIsDelete,FALSE)//等于，即isDelete等于false
                .eq(Article::getIsDraft,FALSE)
                .lt(Article::getId,articleId)//lt是小于，参数：列名，值
                .orderByDesc(Article::getId)//按id排序
                .last("limit 1"));//无视优化规则直接拼接到 sql 的最后(有sql注入的风险,请谨慎使用)
        Article nextArticle = articleDao.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId,Article::getArticleTitle,Article::getArticleCover)
                .eq(Article::getIsDelete,FALSE)
                .eq(Article::getIsDraft,FALSE)
                .gt(Article::getId,articleId)//gt大于
                .orderByDesc(Article::getId)
                .last("limit 1"));
        //把article转换为ArticlePaginationDTO类型
        article.setLastArticle(BeanCopyUtil.copyObject(lastArticle,ArticlePaginationDTO.class));
        article.setNextArticle(BeanCopyUtil.copyObject(nextArticle,ArticlePaginationDTO.class));
        // 查询相关推荐文章
        article.setArticleRecommendList(articleDao.listArticleRecommends(articleId));
        // 封装点赞量和浏览量
        article.setViewsCount((Integer) redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).get(articleId.toString()));
        article.setLikeCount((Integer) redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).get(articleId.toString()));
        return article;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveArticleLike(Integer articleId) {
        // 查询当前用户点赞过的文章id集合
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(ARTICLE_USER_LIKE).get(UserUtil.getLoginUser().getUserInfoId().toString());
        // 第一次点赞则创建
        if(CollectionUtils.isEmpty(articleLikeSet)){
            articleLikeSet = new HashSet<>();
        }
        // 判断是否点赞
        if(articleLikeSet.contains(articleId)){
            // 点过赞则删除文章id
            articleLikeSet.remove(articleId);
            // 文章点赞量-1
            redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).increment(articleId.toString(),-1);
        }
        else{
            // 未点赞则增加文章id
            articleLikeSet.add(articleId);
            // 文章点赞量+1
            redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).increment(articleId.toString(),1);
        }
        // 保存点赞记录
        redisTemplate.boundHashOps(ARTICLE_USER_LIKE).put(UserUtil.getLoginUser().getUserInfoId().toString(),articleLikeSet);
        return 1;
    }

    @Override
    public List<ArticleRecommendDTO> listNewestArticles() {
        List<Article> articleList = articleDao.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getId,Article::getArticleTitle,Article::getArticleCover,Article::getCreateTime)
                .eq(Article::getIsDelete,FALSE)
                .eq(Article::getIsDraft,FALSE)
                .orderByDesc(Article::getId)
                .last("limit 4"));
        return BeanCopyUtil.copyList(articleList,ArticleRecommendDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        //保存或修改文章
        Article article = Article.builder()
                .id(articleVO.getId())
                .userId(UserUtil.getLoginUser().getUserInfoId())
                .categoryId(articleVO.getCategoryId())
//                .articleCover(articleVO.getArticleCover())
                .articleCover(articleVO.getArticleCover().equals("") ? randomCoverUtil.get() : articleVO.getArticleCover())
//                .articleCover(articleVO.getArticleCover().equals("") ? new RandomCoverUtil(redisTemplate).get() : articleVO.getArticleCover())
                .articleTitle(articleVO.getArticleTitle())
                .articleContent(articleVO.getArticleContent())
                .createTime(Objects.isNull(articleVO.getId()) ? new Date() : null)
//                .upDateTime(Objects.nonNull(articleVO.getId()) ? new Date() : null)
                .updateTime(new Date())
                .isTop(articleVO.getIsTop())
                .isDraft(articleVO.getIsDraft())
                .build();
        articleService.saveOrUpdate(article);
        // 编辑文章则删除文章所有标签
        if(Objects.nonNull(articleVO.getId()) && articleVO.getIsDraft().equals(FALSE)){
            articleTagDao.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId,articleVO.getId()));
        }
        // 添加文章标签
        if(!articleVO.getTagIdList().isEmpty()){
            List<ArticleTag> articleTagList = articleVO.getTagIdList().stream().map(tagId -> ArticleTag.builder()
                    .articleId(article.getId())
                    .tagId(tagId)
                    .build())
                    .collect(Collectors.toList());
            articleTagService.saveBatch(articleTagList);
        }
    }

    @Override
    public PageDTO<ArticleBackDTO> listArticleBackDTO(ConditionVO conditionVO) {
        // 转换页码
//        log.info("conditionVO的值{}",conditionVO);
        conditionVO.setCurrent((conditionVO.getCurrent() -1) * conditionVO.getSize());
        // 查询文章总量
        Integer count = articleDao.countArticleBacks(conditionVO);
        if(count == 0)
            return new PageDTO<>();
        // 查询后台文章
        List<ArticleBackDTO> articleBackDTOList = articleDao.listArticleBacks(conditionVO);
        // 查询文章点赞量和浏览量
        Map<String,Integer> viewsCountMap = redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).entries();
        Map<String,Integer> likeCountMap = redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).entries();
        // 封装点赞量和浏览量
        articleBackDTOList.forEach(item -> {
            item.setViewsCount(Objects.requireNonNull(viewsCountMap).get(item.getId().toString()));
            item.setLikeCount(Objects.requireNonNull(likeCountMap).get(item.getId().toString()));
        });
        return new PageDTO<>(articleBackDTOList,count);
    }

    @Override
    public ArticleVO getArticleBackById(Integer articleId) {
        // 查询文章信息
        Article article = articleDao.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId,Article::getArticleTitle,Article::getArticleContent,Article::getArticleCover,Article::getCategoryId,Article::getIsTop,Article::getIsDraft)
                .eq(Article::getId,articleId));
        // 查询文章标签
        List<Integer> tagIdList = articleTagDao.selectList(new LambdaQueryWrapper<ArticleTag>()
                .select(ArticleTag::getTagId)
                .eq(ArticleTag::getArticleId , article.getId()))
                .stream()
                .map(ArticleTag::getTagId).collect(Collectors.toList());
        return ArticleVO.builder()
                .id(article.getId())
                .articleTitle(article.getArticleTitle())
                .articleContent(article.getArticleContent())
                .articleCover(article.getArticleCover())
                .categoryId(article.getCategoryId())
                .isTop(article.getIsTop())
                .tagIdList(tagIdList)
                .isDraft(article.getIsDraft())
                .build();
    }

    @Override
    public ArticleOptionDTO listArticleOptionDTO() {
        // 查询文章分类选项
        List<Category> categoryList = categoryDao.selectList(new LambdaQueryWrapper<Category>()
                .select(Category::getId,Category::getCategoryName));
        List<CategoryBackDTO> categoryBackDTOList = BeanCopyUtil.copyList(categoryList,CategoryBackDTO.class);
        // 查询文章标签选项
        List<Tag> tagList = tagDao.selectList(new LambdaQueryWrapper<Tag>()
                .select(Tag::getId,Tag::getTagName));
        List<TagDTO> tagDTOList = BeanCopyUtil.copyList(tagList,TagDTO.class);
        return ArticleOptionDTO.builder()
                .categoryDTOList(categoryBackDTOList)
                .tagDTOList(tagDTOList)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateArticleTop(Integer articleId, Integer isTop) {
        Article article = Article.builder()
                .id(articleId)
                .isTop(isTop)
                .build();
        articleDao.updateById(article);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateArticleDelete(DeleteVO deleteVO) {
        // 修改文章逻辑删除状态
        List<Article> articleList = deleteVO.getIdList().stream().map(id -> Article.builder()
                .id(id)
                .isTop(FALSE)
                .isDelete(deleteVO.getIsDelete())
                .build())
                .collect(Collectors.toList());
        articleService.updateBatchById(articleList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteArticles(List<Integer> articleIdList) {
        // 删除文章标签关联
        articleTagDao.delete(new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId,articleIdList));
        // 删除文章
        articleDao.deleteBatchIds(articleIdList);
    }

    /**
     * 搜索文章 by zzy
     * @param condition 搜索条件
     * @return
     */
    @Override
    public List<ArticleSearchDTO> listArticleBySearch(ConditionVO condition) {
        if(condition.getKeywords() != null){
            List<Article> articleList = articleDao.selectList(new LambdaQueryWrapper<Article>().
                    select(Article::getId,Article::getArticleTitle,Article::getArticleContent).like(Article::getArticleTitle,condition.getKeywords())
                    .or().like(Article::getArticleContent,condition.getKeywords()).eq(Article::getIsDelete,FALSE));
            List<ArticleSearchDTO> articleSearchDTOList = BeanCopyUtil.copyList(articleList, ArticleSearchDTO.class);
            return articleSearchDTOList;
        }
        return null;
    }

    /**
     *更新文章浏览量
     * @param articleId 文章id
     */
    @Async
    public void updateArticleViewsCount(Integer articleId){
        // 判断是否第一次访问，增加浏览量
        Set<Integer> set = (Set<Integer>) session.getAttribute("articleSet");
        if(Objects.isNull(set))
            set = new HashSet<>();
        if(!set.contains(articleId)){
            set.add(articleId);
            session.setAttribute("articleSet",set);
            //浏览量++
            redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).increment(articleId.toString(),1);
        }
    }
}
