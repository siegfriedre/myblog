package com.siegfried.blog.controller;

import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.MessageDTO;
import com.siegfried.blog.service.MessageService;
import com.siegfried.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/4.
 */
@Api(tags = "留言板")
@RestController
public class MessageController {

    @Autowired
    MessageService messageService;

    @ApiOperation("查看留言列表")
    @GetMapping("/message")
    public Result<List<MessageDTO>> listMessages(){
        return new Result<>(true, StatusConst.OK,"查询成功",messageService.listMessages());
    }
}
