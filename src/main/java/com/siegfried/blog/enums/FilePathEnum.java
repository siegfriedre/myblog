package com.siegfried.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by zy_zhu on 2021/5/12.
 */
@Getter
@AllArgsConstructor
public enum FilePathEnum {

    /**
     * 头像路径
     */
    AVATAR("images/avatar/", "头像路径"),
    /**
     * 文章图片路径
     */
    ARTICLE("images/article_cover/", "文章图片路径"),
    /**
     * 音频路径
     */
    VOICE("voice/", "音频路径");

    /**
     * 路径
     */
    private final String path;

    /**
     * 描述
     */
    private final String desc;
}
