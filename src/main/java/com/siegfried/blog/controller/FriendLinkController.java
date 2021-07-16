package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.FriendLinkBackDTO;
import com.siegfried.blog.dto.FriendLinkDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.service.FriendLinkService;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.FriendLinkVO;
import com.siegfried.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.siegfried.blog.constant.OptTypeConst.REMOVE;
import static com.siegfried.blog.constant.OptTypeConst.SAVE_OR_UPDATE;

/**
 * Created by zy_zhu on 2021/5/3.
 */
@Api(tags = "友情链接")
@RestController
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    @ApiOperation("查看友情链接列表")
    @GetMapping("/links")
    public Result<List<FriendLinkDTO>> listFriendLinks(){
        return new Result<>(true, StatusConst.OK,"查询成功",friendLinkService.listFriendLinks());
    }

    //------------后台接口-----------------

    @ApiOperation(value = "查看后台友链列表")
    @GetMapping("/admin/links")
    public Result<PageDTO<FriendLinkBackDTO>> listFriendLinkDTO(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",friendLinkService.listFriendLinkDTO(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "保存或修改友链")
    @PostMapping("/admin/links")
    public Result saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO){
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除友链")
    @DeleteMapping("/admin/links")
    public Result deleteFriendLink(@RequestBody List<Integer> linkIdList){
        friendLinkService.removeByIds(linkIdList);
        return new Result(true,StatusConst.OK,"删除成功");
    }
}
