package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/3.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePreviewListDTO {
    /**
     * 条件对应的文章列表
     */
    private List<ArticlePreviewDTO> articlePreviewDTOList;

    /**
     * 条件名
     */
    private String name;
}
