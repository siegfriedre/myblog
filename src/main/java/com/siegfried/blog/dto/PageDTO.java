package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对分页进行一个封装，包含一个内容的list和条目总数
 * Created by zy_zhu on 2021/4/21.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO <T>{

    /**
     * 分页列表
     */
    private List<T> recordList;

    /**
     * 总数
     */
    private Integer count;
}
