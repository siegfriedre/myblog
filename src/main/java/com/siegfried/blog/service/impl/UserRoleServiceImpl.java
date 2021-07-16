package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.UserRoleDao;
import com.siegfried.blog.entity.UserRole;
import com.siegfried.blog.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author zy_zhu
 * @date 2021/5/22 22:40
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleDao,UserRole> implements UserRoleService {
}
