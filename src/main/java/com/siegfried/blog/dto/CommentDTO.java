package com.siegfried.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/7/12 19:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    /**
     *评论id
     */
    private Integer id;

    /**
     *用户id
     */
    private Integer userId;

    /**
     *用户头像
     */
    private String avatar;

    /**
     *用户网站
     */
    private String webSite;

    /**
     *用户昵称
     */
    private String nickname;

    /**
     *评论内容
     */
    private String commentContent;

    /**
     *评论时间
     */
    private Date createTime;

    /**
     *点赞数
     */
    private Integer likeCount;

    /**
     *回复数
     */
    private Integer replyCount;

    /**
     *回复列表
     */
    private List<ReplyDTO> replyDTOList;
}
