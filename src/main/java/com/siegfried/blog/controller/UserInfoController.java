package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.UserOnlineDTO;
import com.siegfried.blog.service.UserinfoService;
import com.siegfried.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static com.siegfried.blog.constant.OptTypeConst.UPDATE;

/**
 * Created by zy_zhu on 2021/5/12.
 */
@Api(tags = "用户信息模块")
@RestController
public class UserInfoController {
    @Autowired
    private UserinfoService userinfoService;

    @ApiOperation(value = "修改用户资料")
    @PutMapping("/users/info")
    public Result updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO){
        userinfoService.updateUserInfo(userInfoVO);
        return new Result(true, StatusConst.OK,"修改成功");
    }

    @ApiOperation(value = "修改用户头像")
    @ApiImplicitParam(name = "file", value = "用户头像", required = true, dataType = "MultipartFile")
    @PostMapping("/users/avatar")
    public Result<String> updateUserInfo(MultipartFile file){
        return new Result<>(true,StatusConst.OK,"修改成功",userinfoService.updateUserAvatar(file));
    }

    @ApiOperation(value = "绑定用户邮箱")
    @PostMapping("/users/email")
    public Result saveUserEmail(@Valid @RequestBody EmailVO emailVO){
        userinfoService.saveUserEmail(emailVO);
        return new Result(true,StatusConst.OK,"绑定成功");
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改用户角色")
    @PutMapping("/admin/users/role")
    public Result updateUserRole (@Valid @RequestBody UserRoleVO userRoleVO){
        userinfoService.updateUserRole(userRoleVO);
        return new Result(true,StatusConst.OK,"修改成功");
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改用户禁用状态")
    @PutMapping("/admin/users/disable/{userInfoId}")
    public Result updateUserSilence(@PathVariable("userInfoId") Integer userInfoId, Integer isDisable){
        userinfoService.updaupdateUserDisable(userInfoId,isDisable);
        return new Result<>(true,StatusConst.OK,"修改成功");
    }

    @ApiOperation(value = "查看在线用户")
    @GetMapping("/admin/users/online")
    public Result<PageDTO<UserOnlineDTO>> listOnlineUsers(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",userinfoService.listOnlineUsers(conditionVO));
    }

    @ApiOperation(value = "下线用户")
    @DeleteMapping("/admin/users/online/{userInfoId}")
    public Result removeOnlineUser(@PathVariable("userInfoId") Integer userInfoId){
        userinfoService.removeOnlineUser(userInfoId);
        return new Result(true,StatusConst.OK,"操作成功");
    }
}
