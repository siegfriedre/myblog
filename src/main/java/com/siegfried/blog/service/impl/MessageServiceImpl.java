package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.MessageDao;
import com.siegfried.blog.dto.MessageDTO;
import com.siegfried.blog.entity.Message;
import com.siegfried.blog.service.MessageService;
import com.siegfried.blog.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/4.
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements MessageService {

    @Autowired
    MessageDao messageDao;

    @Override
    public List<MessageDTO> listMessages() {
        List<Message> messageList = messageDao.selectList(new LambdaQueryWrapper<Message>()
                .select(Message::getId,Message::getNickname,Message::getAvatar,Message::getMessageContent));
        return BeanCopyUtil.copyList(messageList,MessageDTO.class);
    }
}
