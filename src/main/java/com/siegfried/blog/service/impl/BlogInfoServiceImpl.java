package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siegfried.blog.constant.CommonConst;
import com.siegfried.blog.dao.*;
import com.siegfried.blog.dto.*;
import com.siegfried.blog.entity.Article;
import com.siegfried.blog.entity.UserInfo;
import com.siegfried.blog.service.BlogInfoService;
import com.siegfried.blog.service.UniqueViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.siegfried.blog.constant.RedisPrefixConst.*;

/**
 * Created by zy_zhu on 2021/5/3.
 */
@Service
public class BlogInfoServiceImpl implements BlogInfoService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    ArticleDao articleDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    TagDao tagDao;
    @Autowired
    MessageDao messageDao;
    @Autowired
    UniqueViewService uniqueViewService;

    @Override
    public String getAbout() {
        Object value = redisTemplate.boundValueOps(ABOUT).get();
        return Objects.nonNull(value) ? value.toString() : "";
    }

    @Override
    public BlogHomeInfoDTO getBlogInfo() {
        //查询博主信息
        UserInfo userInfo = userInfoDao.selectOne(new LambdaQueryWrapper<UserInfo>()
                .select(UserInfo::getId,UserInfo::getAvatar,UserInfo::getIntro)
                .eq(UserInfo::getId, CommonConst.BLOGGER_ID));
        // 查询文章数量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete,false)
                .eq(Article::getIsDraft,false));
        // 查询分类数量
        Integer categoryCount = categoryDao.selectCount(null);
        // 查询标签数量
        Integer tagCount = tagDao.selectCount(null);
        // 查询公告
        Object value = redisTemplate.boundValueOps(NOTICE).get();
        String notice = Objects.nonNull(value) ? value.toString():"发布你的第一篇公告吧";
        // 查询访问量

        /*f(redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).get() == null)
            redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).set("1");*/
        String viewsCount = Objects.requireNonNull(redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).get().toString());
        // 封装数据
        return BlogHomeInfoDTO.builder()
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .notice(notice)
                .viewsCount(viewsCount)
                .build();
    }

    @Override
    public BlogBackInfoDTO getBlogBackInfo() {
        // 查询访问量
//        Integer viewsCount = Integer.parseInt(redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).get().toString()) ;
        Integer viewsCount = (Integer) redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).get();
//        System.out.println(viewsCount+"-------------------------");
        // 查询留言量
        Integer messageCount = messageDao.selectCount(null);
        // 查询用户量
        Integer userCount = userInfoDao.selectCount(null);
        // 查询文章量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete,false)
                .eq(Article::getIsDraft,false));
        // 查询一周访问量
        List<UniqueViewDTO> uniqueViewList = uniqueViewService.listUniqueViews();
        // 查询分类数据
        List<CategoryDTO> categoryDTOList = categoryDao.listCategoryDTO();


        // 查询redis访问量前五的文章
        Map<String,Integer> articleViewMap = redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).entries();
        // 将文章进行倒序排序
        List<Integer> articleIdList = Objects.requireNonNull(articleViewMap).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(item -> Integer.valueOf(item.getKey()))
                .collect(Collectors.toList());
        // 提取前五篇文章
        int index = Math.min(articleIdList.size(),5);
        articleIdList = articleIdList.subList(0,index);
        // 文章为空直接返回
        if(articleIdList.isEmpty()){
            return BlogBackInfoDTO.builder()
                    .viewsCount(viewsCount)
                    .messageCount(messageCount)
                    .userCount(userCount)
                    .articleCount(articleCount)
                    .categoryDTOList(categoryDTOList)
                    .uniqueViewDTOList(uniqueViewList)
                    .build();
        }
        // 查询文章标题
        List<Article> articleList = articleDao.listArticleRank(articleIdList);
        // 封装浏览量
        List<ArticleRankDTO> articleRankDTOList = articleList.stream().map(article -> ArticleRankDTO.builder()
                .articleTitle(article.getArticleTitle())
                .viewsCount(articleViewMap.get(article.getId().toString()))
                .build())
                .collect(Collectors.toList());
        return BlogBackInfoDTO.builder()
                .viewsCount(viewsCount)
                .messageCount(messageCount)
                .userCount(userCount)
                .articleCount(articleCount)
                .categoryDTOList(categoryDTOList)
                .uniqueViewDTOList(uniqueViewList)
                .articleRankDTOList(articleRankDTOList)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAbout(String aboutContent) {
        redisTemplate.boundValueOps(ABOUT).set(aboutContent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNotice(String notice) {
        redisTemplate.boundValueOps(NOTICE).set(notice);
    }

    @Override
    public String getNotice() {
        Object value = redisTemplate.boundValueOps(NOTICE).get();
        return Objects.nonNull(value) ? value.toString() : "发布你的第一篇公告吧";
    }

}
