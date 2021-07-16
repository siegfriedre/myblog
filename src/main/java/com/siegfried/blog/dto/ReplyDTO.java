package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zy_zhu
 * @date 2021/7/12 19:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {
    /**
     * 评论id
     */
    private Integer id;

    /**
     * 父评论id
     */
    private Integer parentId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     *用户头像
     */
    private String avatar;

    /**
     *评论内容
     */
    private String commentContent;

    /**
     *用户网站
     */
    private String webSite;

    /**
     *点赞数
     */
    private Integer likeCount;

    /**
     *评论时间
     */
    private Date createTime;

    /**
     *被回复用户id
     */
    private Integer replyId;

    /**
     *被回复用户昵称
     */
    private String replyNickname;

    /**
     *被回复用户网站
     */
    private String replyWebSite;
}
