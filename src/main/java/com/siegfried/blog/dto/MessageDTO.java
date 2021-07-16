package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zy_zhu on 2021/5/4.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String messageContent;
//    private Integer time;
}
