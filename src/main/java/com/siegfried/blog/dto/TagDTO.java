package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签
 * Created by zy_zhu on 2021/4/21.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * tagName
     */
    private String tagName;
}
