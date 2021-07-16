package com.siegfried.blog.constant;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * 公共常量
 *
 * Created by zy_zhu on 2021/4/21.
 */
public class CommonConst {

    public static final int FALSE = 0;
    public static final int TRUE = 1;
    /**
     * 博主id
     */
    public static final int BLOGGER_ID = 1;

    /**
     * 默认用户昵称
     */
    public static final String DEFAULT_NICKNAME = "用户" + IdWorker.getId();

    /**
     * 默认用户头像
     */
    public static final String DEFAULT_AVATAR = "https://www.static.talkxj.com/avatar/user.png";

    /**
     * 前端组件名
     */
    public static String COMPONENT = "Layout";

    /**
     * 网站域名
     */
    public static final String URL = "http://jelliam.top";

    /**
     * 文章页面路径
     */
    public static final String ARTICLE_PATH = "/articles/";

    /**
     * 友联页面路径
     */
    public static final String LINK_PATH = "/links";

}
