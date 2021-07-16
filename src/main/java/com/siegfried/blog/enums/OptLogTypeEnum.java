package com.siegfried.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy_zhu
 * @date 2021/5/23 15:15
 */
@Getter
@AllArgsConstructor
public enum OptLogTypeEnum {

    /**
     * 新增
     */
    ADD(1,"新增"),
    /**
     * 修改
     */
    UPDATE(2,"修改"),
    /**
     * 删除
     */
    REMOVE(3,"删除");
    /**
     * 类型
     */
    private final int type;

    /**
     * 描述
     */
    private final String desc;
}
