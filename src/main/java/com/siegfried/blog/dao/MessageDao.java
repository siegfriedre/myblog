package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.entity.Message;
import org.springframework.stereotype.Repository;

/**
 * Created by zy_zhu on 2021/5/4.
 */
@Repository
public interface MessageDao extends BaseMapper<Message> {

}
