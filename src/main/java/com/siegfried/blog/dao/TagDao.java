package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.entity.Tag;
import org.springframework.stereotype.Repository;



/**
 * Created by zy_zhu on 2021/5/3.
 */
@Repository
public interface TagDao extends BaseMapper<Tag> {
}
