package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章上（下）篇的DTO
 * Created by zy_zhu on 2021/5/4.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePaginationDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 标题
     */
    private String articleTitle;
}
