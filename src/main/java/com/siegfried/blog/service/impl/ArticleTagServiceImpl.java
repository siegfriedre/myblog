package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.ArticleTagDao;
import com.siegfried.blog.entity.ArticleTag;
import com.siegfried.blog.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * Created by zy_zhu on 2021/5/7.
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagDao, ArticleTag> implements ArticleTagService {

}
