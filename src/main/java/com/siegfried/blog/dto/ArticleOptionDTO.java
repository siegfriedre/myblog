package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章选项
 *
 * Created by zy_zhu on 2021/5/8.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleOptionDTO {

    /**
     * 文章标签列表
     */
    private List<TagDTO> tagDTOList;

    /**
     * 文章分类列表
     */
    private List<CategoryBackDTO> categoryDTOList;
}
