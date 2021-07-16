package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.CommentBackDTO;
import com.siegfried.blog.dto.CommentDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.ReplyDTO;
import com.siegfried.blog.entity.Comment;
import com.siegfried.blog.vo.CommentVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.DeleteVO;

import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/7/12 19:33
 */
public interface CommentService extends IService<Comment> {

    /**
     * 查询评论列表
     * @param articleId 文章id
     * @param current 当前页码
     * @return
     */
    PageDTO<CommentDTO> listComments(Integer articleId,Long current);

    /**
     * 查询评论回复列表
     * @param commentId 父评论id
     * @param current 当前页码
     * @return
     */
    List<ReplyDTO> listRepliesByCommentId(Integer commentId,Long current);

    /**
     * 添加评论或回复
     * @param commentVO 前台提交的评论对象
     */
    void saveComment(CommentVO commentVO);

    /**
     * 点赞评论
     * @param commentId 评论id
     */
    void saveCommentLike(Integer commentId);

    /**
     * 后台获取评论
     * @param conditionVO 条件
     * @return 评论分页对象
     */
    PageDTO<CommentBackDTO> listCommentBackDTO(ConditionVO conditionVO);

    /**
     * 删除或恢复评论
     * @param deleteVO 逻辑删除对象
     */
    void updateCommentDelete(DeleteVO deleteVO);

}
