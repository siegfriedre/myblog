package com.siegfried.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.CommentDao;
import com.siegfried.blog.dao.UserInfoDao;
import com.siegfried.blog.dto.*;
import com.siegfried.blog.entity.Comment;
import com.siegfried.blog.service.CommentService;
import com.siegfried.blog.service.RedisService;
import com.siegfried.blog.utils.HTMLUtil;
import com.siegfried.blog.utils.UserUtil;
import com.siegfried.blog.vo.CommentVO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.DeleteVO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.siegfried.blog.constant.CommonConst.*;
import static com.siegfried.blog.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.siegfried.blog.constant.RedisPrefixConst.COMMENT_LIKE_COUNT;
import static com.siegfried.blog.constant.RedisPrefixConst.COMMENT_USER_LIKE;

/**
 * @author zy_zhu
 * @date 2021/7/12 19:35
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, Comment> implements CommentService {

    @Autowired
    CommentDao commentDao;
    @Autowired
    RedisService redisService;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageDTO<CommentDTO> listComments(Integer articleId, Long current) {
        //获取评论条数，如果文章id不空那就查不空的
        Integer commentCount = commentDao.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(articleId != null,Comment::getArticleId,articleId)
                .eq(Comment::getIsDelete,FALSE)
                .isNull(Comment::getParentId));
//                .isNull(articleId == null,Comment::getArticleId));
        if(commentCount == 0)
            return new PageDTO<>();
        //获取评论列表，但部分项（点赞，回复等）是空的，下面才对其进行封装
        List<CommentDTO> commentDTOList = commentDao.listComments(articleId,(current-1)*10);//之所以要-1是因为sql语句的limit限制

        //获取redis评论点赞数. Map<id,count>
        Map<String,Integer> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        //----------提取出评论的id集合
        List<Integer> commentIdList = new ArrayList<>();
        //----------封装评论点赞数目
        commentDTOList.forEach(item -> {
            commentIdList.add(item.getId());
            item.setLikeCount(Objects.requireNonNull(likeCountMap).get(item.getId().toString()));//如果likeCountMap非空则获取当前item的点赞数
        });
        //根据评论id集合查询回复数据
        List<ReplyDTO> replyDTOList = commentDao.listReplies(commentIdList);//这里只查询了前3条，剩余的通过另一个请求（查看更多回复）来查看
        //封装回复点赞量
        replyDTOList.forEach(item -> item.setLikeCount(Objects.requireNonNull(likeCountMap).get(item.getId().toString())));
        //根据评论id把回复数据分组
        Map<Integer,List<ReplyDTO>> replyMap = replyDTOList.stream().collect(Collectors.groupingBy(ReplyDTO::getParentId));
        //根据评论id查询回复量
        Map<Integer,Integer> replyCountMap = commentDao.listReplyCountByCommentId(commentIdList)
                .stream().collect(Collectors.toMap(ReplyCountDTO::getCommentId,ReplyCountDTO::getReplyCount));
        //将分页回复数据和回复量封装进对应的评论
        commentDTOList.forEach(item ->{
            item.setReplyDTOList(replyMap.get(item.getId()));
            item.setReplyCount(replyCountMap.get(item.getId()));
        });
        return new PageDTO<>(commentDTOList,commentCount);
    }

    @Override
    public List<ReplyDTO> listRepliesByCommentId(Integer commentId, Long current) {
        //获取回复列表
        List<ReplyDTO> replyDTOList = commentDao.listRepliesByCommentId(commentId,(current-1)*5);
        //获取redis评论点赞数. Map<id,count>
        Map<String,Integer> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        //封装点赞数据
        replyDTOList.forEach(item -> item.setLikeCount(Objects.requireNonNull(likeCountMap).get(item.getId().toString())));
        return replyDTOList;
    }

    @Override
    public void saveComment(CommentVO commentVO) {
        // 过滤html标签
        commentVO.setCommentContent(HTMLUtil.deleteCommentTag(commentVO.getCommentContent()));
        Comment comment = Comment.builder()
                .userId(UserUtil.getLoginUser().getUserInfoId())
                .articleId(commentVO.getArticleId())
                .commentContent(commentVO.getCommentContent())
                .createTime(new Date())
                .replyId(commentVO.getReplyId())
                .parentId(commentVO.getParentId())
                .isDelete(FALSE)
                .build();
        commentDao.insert(comment);

        // 发送邮件通知用户
        notice(commentVO);
    }

    /**
     * 通知评论用户
     *
     * @param commentVO 评论信息
     */
    @Async
    public void notice(CommentVO commentVO){
        // -------判断用户是回复别人还是评论作者
        Integer userId = Objects.nonNull(commentVO.getReplyId()) ? commentVO.getReplyId() : BLOGGER_ID;
        // -------查询邮箱号
        String email = userInfoDao.selectById(userId).getEmail();
        if(email != null){
            // -------获取评论页面直达路径
            String url = Objects.nonNull(commentVO.getArticleId()) ? URL+ARTICLE_PATH+commentVO.getArticleId() : URL + MESSAGE_PATH;
            // -------发送邮件
            EmailDTO emailDTO = EmailDTO.builder()
                    .email(email)
                    .subject("评论提醒")
                    .content("您收到了一条新的回复，请前往\n" + url + "\n页面查看")
                    .build();
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
            System.out.println("-----------------------------"+email);
        }
    }

    @Override
    public void saveCommentLike(Integer commentId) {
        // 查询当前用户点赞过的评论id集合
        HashSet<Integer> commentLikeSet = (HashSet<Integer>) redisService.hGet(COMMENT_USER_LIKE, UserUtil.getLoginUser().getUserInfoId().toString());
        if(commentLikeSet == null)
            commentLikeSet = new HashSet<Integer>();
        if(commentLikeSet.contains(commentId)){
            commentLikeSet.remove(commentId);
            // 评论点赞量-1
            redisService.hDecr(COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        }
        else{
            commentLikeSet.add(commentId);
            // 评论点赞量+1
            redisService.hIncr(COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        }
        redisService.hSet(COMMENT_USER_LIKE,UserUtil.getLoginUser().getUserInfoId().toString(),commentLikeSet);
    }

    @Override
    public PageDTO<CommentBackDTO> listCommentBackDTO(ConditionVO conditionVO) {
        //修改一下分页数据方便查询
        conditionVO.setCurrent((conditionVO.getCurrent()-1)*conditionVO.getSize());
        List<CommentBackDTO> commentBackDTOList = commentDao.listCommentBackDTO(conditionVO);
        Integer commentBackDTOCount = commentDao.countCommentDTO(conditionVO);
        //封装点赞数据
        Map<String,Integer> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        commentBackDTOList.forEach(item -> item.setLikeCount(Objects.requireNonNull(likeCountMap).get(item.getId().toString())));
        return new PageDTO<>(commentBackDTOList,commentBackDTOCount);
    }

    @Override
    public void updateCommentDelete(DeleteVO deleteVO) {
        // 修改评论逻辑删除状态
        List<Comment> commentList = deleteVO.getIdList().stream()
                .map(id -> Comment.builder().id(id).isDelete(deleteVO.getIsDelete()).build())
                .collect(Collectors.toList());
        this.updateBatchById(commentList);
    }

    @Override
    public PageDTO<CommentDTO> listMessages(Long current) {
        List<CommentDTO> commentDTOList = commentDao.listComments(null,(current-1)*10);
        Integer count = commentDao.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getIsDelete,FALSE)
                .isNull(Comment::getParentId)
                .isNull(Comment::getArticleId));
        if(count == 0)
            return new PageDTO<>();
        Map<String,Integer> commentLikeMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        List<Integer> commentIdList = new ArrayList<>();
        commentDTOList.forEach(item -> {
            commentIdList.add(item.getId());
            item.setLikeCount(Objects.requireNonNull(commentLikeMap).get(item.getId().toString()));
        });
        List<ReplyDTO> replyDTOList = commentDao.listReplies(commentIdList);
        replyDTOList.forEach(item -> item.setLikeCount(Objects.requireNonNull(commentLikeMap).get(item.getId().toString())));
        //根据评论id把回复数据分组
        Map<Integer,List<ReplyDTO>> replyMap = replyDTOList.stream().collect(Collectors.groupingBy(ReplyDTO::getParentId));
        //根据评论id查询回复量
        Map<Integer,Integer> replyCountMap = commentDao.listReplyCountByCommentId(commentIdList)
                .stream().collect(Collectors.toMap(ReplyCountDTO::getCommentId,ReplyCountDTO::getReplyCount));
        //封装
        commentDTOList.forEach( item -> {
            item.setReplyDTOList(replyMap.get(item.getId()));
            item.setReplyCount(replyCountMap.get(item.getId()));
        });
        return new PageDTO<>(commentDTOList,count);
    }

    @Override
    public PageDTO<CommentBackDTO> listMessageBackDTO(ConditionVO conditionVO) {
        //修改一下分页数据方便查询
        conditionVO.setCurrent((conditionVO.getCurrent()-1)*conditionVO.getSize());
        List<CommentBackDTO> commentBackDTOList = commentDao.listMessageBackDTO(conditionVO);
        Integer commentBackDTOCount = commentDao.countCommentDTO(conditionVO);
        //封装点赞数据
        Map<String,Integer> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        commentBackDTOList.forEach(item -> item.setLikeCount(Objects.requireNonNull(likeCountMap).get(item.getId().toString())));
        return new PageDTO<>(commentBackDTOList,commentBackDTOCount);
    }
}
