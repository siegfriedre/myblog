package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问量DTO
 * Created by zy_zhu on 2021/5/7.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UniqueViewDTO {
    /**
     * 日期
     */
    private String day;

    /**
     * 访问量
     */
    private Integer viewsCount;
}
