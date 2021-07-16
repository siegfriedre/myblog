package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.dto.CommentBackDTO;
import com.siegfried.blog.dto.CommentDTO;
import com.siegfried.blog.dto.ReplyCountDTO;
import com.siegfried.blog.dto.ReplyDTO;
import com.siegfried.blog.entity.Comment;
import com.siegfried.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/7/12 19:38
 */
public interface CommentDao extends BaseMapper<Comment> {

    /**
     * 文章评论列表
     * @param articleId 文章id
     * @param current 当前页码
     * @return 10条评论
     */
    List<CommentDTO> listComments(@Param("articleId") Integer articleId, @Param("current") Long current);

    /**
     * 评论回复列表（只要前三个样例）
     * @param commentIdList 父评论id集合
     * @return 子评论（回复）集合
     */
    List<ReplyDTO> listReplies(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 评论回复数量
     * @param commentIdList 父评论id集合
     * @return 回复数量
     */
    List<ReplyCountDTO> listReplyCountByCommentId(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 通过评论id获取回复列表（完整的）
     * @param commentId 父评论id
     * @param current 当前页码
     * @return 回复列表（完整的）
     */
    List<ReplyDTO> listRepliesByCommentId(@Param("commentId") Integer commentId,@Param("current") Long current);

    /**
     * 后台获取评论列表
     * @param conditionVO 条件
     * @return 评论列表
     */
    List<CommentBackDTO> listCommentBackDTO(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 后台获取评论数目
     * @param conditionVO 条件
     * @return 数目
     */
    Integer countCommentDTO(@Param("conditionVO") ConditionVO conditionVO);
}
