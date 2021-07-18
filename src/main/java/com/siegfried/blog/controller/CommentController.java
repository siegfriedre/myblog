package com.siegfried.blog.controller;

import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.CommentBackDTO;
import com.siegfried.blog.dto.CommentDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.ReplyDTO;
import com.siegfried.blog.service.CommentService;
import com.siegfried.blog.vo.CommentVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.DeleteVO;
import com.siegfried.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/7/12 19:14
 */
@Api(tags = "comment")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "查询评论")
    @GetMapping("/comments")
    public Result<PageDTO<CommentDTO>> listComments(Integer articleId, Long current){
        return new Result<>(true, StatusConst.OK,"查询成功",commentService.listComments(articleId,current));
    }

    @ApiOperation(value = "查看评论下的更多回复")
    @GetMapping("/comments/replies/{commentId}")
    public Result<List<ReplyDTO>> listRepliesByCommentId(@PathVariable("commentId") Integer commentId, Long current){
        return new Result<>(true,StatusConst.OK,"查询成功",commentService.listRepliesByCommentId(commentId,current));
    }

    @ApiOperation(value = "添加评论或回复")
    @PostMapping("/comments")
    public Result saveComment(@Valid @RequestBody CommentVO commentVO){
        commentService.saveComment(commentVO);
        return new Result(true,StatusConst.OK,"评论成功");
    }

    @ApiOperation(value = "评论点赞")
    @PostMapping("/comments/like")
    public Result saveCommentLike(Integer commentId){
        commentService.saveCommentLike(commentId);
        return new Result(true,StatusConst.OK,"点赞成功");
    }

    @ApiOperation(value = "查看留言板")
    @GetMapping("/message")
    public Result<PageDTO<CommentDTO>> listMessages(Long current){
        return new Result<>(true,StatusConst.OK,"查询成功",commentService.listMessages(current));
    }

    //--------------------后台接口----------------------

    @ApiOperation(value = "查询后台评论")
    @GetMapping("/admin/comments")
    public Result<PageDTO<CommentBackDTO>> listCommentBackDTO(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",commentService.listCommentBackDTO(conditionVO));
    }

    @ApiOperation(value = "删除或恢复评论")
    @PutMapping("/admin/comments")
    public Result deleteComment(DeleteVO deleteVO){
        commentService.updateCommentDelete(deleteVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @ApiOperation(value = "物理删除评论")
    @DeleteMapping("/admin/comments")
    public Result deleteComment(@RequestBody List<Integer> commentIdList){
        commentService.removeByIds(commentIdList);
        return new Result(true,StatusConst.OK,"删除成功");
    }

    @ApiOperation(value = "查询后台评论")
    @GetMapping("/admin/message")
    public Result<PageDTO<CommentBackDTO>> listMessageBackDTO(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",commentService.listMessageBackDTO(conditionVO));
    }
}
