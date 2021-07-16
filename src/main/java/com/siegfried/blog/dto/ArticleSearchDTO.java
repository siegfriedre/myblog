package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zy_zhu
 * @date 2021/7/12 12:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleSearchDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 是否删除
     */
    private Integer isDelete;
}
