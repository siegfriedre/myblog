package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 推荐文章
 * Created by zy_zhu on 2021/5/4.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRecommendDTO {
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

    /**
     * 创建时间
     */
    private Date createTime;

}
