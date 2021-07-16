package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zy_zhu on 2021/5/3.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendLinkDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * 链接名字
     */
    private String linkName;

    /**
     * 链接头像
     */
    private String linkAvatar;

    /**
     * 链接地址
     */
    private String linkAddress;

    /**
     * 链接介绍
     */
    private String linkIntro;
}
