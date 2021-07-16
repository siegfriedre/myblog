package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.BlogBackInfoDTO;
import com.siegfried.blog.dto.BlogHomeInfoDTO;

/**
 * Created by zy_zhu on 2021/5/3.
 */
public interface BlogInfoService{
    /**
     * 关于我
     * @return 关于我内容
     */
    String getAbout();

    /**
     * 获取首页博客信息的数据
     * @return 博客信息
     */
    BlogHomeInfoDTO getBlogInfo();

    /**
     * 获取后台首页的数据
     * @return 博客信息
     */
    BlogBackInfoDTO getBlogBackInfo();

    /**
     * 修改关于我信息
     * @param aboutContent 新的内容
     */
    void updateAbout(String aboutContent);

    /**
     * 修改公告
     * @param notice 新的公告
     */
    void updateNotice(String notice);

    /**
     * 查看公告
     */
    String getNotice();
}
