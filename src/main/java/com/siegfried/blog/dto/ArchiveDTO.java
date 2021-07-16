package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 数据传输对象，实现了一个封装和筛选（比如entity的某些属性我们这里是不需要的）
 *
 * Created by zy_zhu on 2021/4/21.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * title
     */
    private String articleTitle;

    /**
     * createTime
     */
    private Date createTime;
}
