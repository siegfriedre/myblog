package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回复数量
 * @author zy_zhu
 * @date 2021/7/13 20:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyCountDTO {

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 回复数量
     */
    private Integer replyCount;
}
