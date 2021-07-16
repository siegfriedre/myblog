package com.siegfried.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy_zhu
 * @date 2021/5/23 15:13
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    /**
     * 邮箱登录
     */
    EMAIL(0, "邮箱登录"),
    /**
     * QQ登录
     */
    QQ(1, "QQ登录"),
    /**
     * 微博登录
     */
    WEIBO(2, "微博登录");

    /**
     * 登录方式
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

}
