package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.entity.UserInfo;
import org.springframework.stereotype.Repository;

/**
 * Created by zy_zhu on 2021/5/7.
 */
@Repository
public interface UserInfoDao extends BaseMapper<UserInfo> {

}
