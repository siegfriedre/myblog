package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台分类
 *
 * Created by zy_zhu on 2021/5/8.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBackDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;
}
