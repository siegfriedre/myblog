package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.MessageDTO;
import com.siegfried.blog.entity.Message;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/4.
 */
public interface MessageService extends IService<Message> {
    /**
     * 查看留言列表
     * @return 留言列表
     */
    List<MessageDTO> listMessages();
}
