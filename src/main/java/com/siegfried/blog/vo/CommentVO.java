package com.siegfried.blog.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author zy_zhu
 * @date 2021/7/14 15:28
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {

    /**
     * 被回复用户id
     */
    @ApiModelProperty(name = "replyId", value = "回复用户id", dataType = "Integer")
    private Integer replyId;

    /**
     * 评论文章id
     */
    @ApiModelProperty(name = "articleId", value = "文章id", dataType = "Integer")
    private Integer articleId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @ApiModelProperty(name = "commentContent", value = "评论内容", required = true, dataType = "String")
    private String commentContent;

    /**
     * 父评论id
     */
    @ApiModelProperty(name = "parentId", value = "父评论id", dataType = "Integer")
    private Integer parentId;
}