package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 首页文章列表
 * Created by zy_zhu on 2021/4/21.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleHomeDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * Cover
     */
    private String articleCover;

    /**
     * title
     */
    private String articleTitle;

    /**
     * content
     */
    private String articleContent;

    /**
     * createTime
     */
    private Date createTime;

    /**
     * top
     */
    private Boolean isTop;

    /**
     * categoryId
     */
    private Integer categoryId;

    /**
     * categoryName
     */
    private String categoryName;

    /**
     * tag
     */
    private List<TagDTO> tagDTOList;
}
