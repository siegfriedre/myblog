package com.siegfried.blog.controller;

import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.UserBackDTO;
import com.siegfried.blog.service.UserAuthService;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.PasswordVO;
import com.siegfried.blog.vo.Result;
import com.siegfried.blog.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author zy_zhu
 * @date 2021/5/23 14:45
 */
@Api(tags = "用户账号模块")
@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @ApiOperation(value = "发送邮箱验证码")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @GetMapping("/users/code")
    public Result sendCode(String username){
        userAuthService.sendCode(username);
        return new Result(true,StatusConst.OK,"发送成功");
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/users")
    public Result saveUser(@Valid @RequestBody UserVO user){
        userAuthService.saveUser(user);
        return new Result(true, StatusConst.OK,"注册成功");
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("/users/password")
    public Result updatePassword(@Valid @RequestBody UserVO user){
        userAuthService.updatePassword(user);
        return new Result(true,StatusConst.OK,"修改成功");
    }

    @ApiOperation(value = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public Result updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO){
        userAuthService.updateAdminPassword(passwordVO);
        return new Result<>(true, StatusConst.OK, "修改成功！");
    }

    @ApiOperation(value = "查看后台用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<UserBackDTO>> listUsers(ConditionVO condition){
        return new Result<>(true,StatusConst.OK,"查询成功",userAuthService.listUserBackDTO(condition));
    }
}
