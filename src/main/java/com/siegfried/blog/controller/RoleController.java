package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.RoleDTO;
import com.siegfried.blog.dto.UserRoleDTO;
import com.siegfried.blog.service.RoleService;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.Result;
import com.siegfried.blog.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.siegfried.blog.constant.OptTypeConst.REMOVE;
import static com.siegfried.blog.constant.OptTypeConst.SAVE_OR_UPDATE;

/**
 * Created by zy_zhu on 2021/5/9.
 */
@Api(tags = "角色模块(管理员，用户，测试)")
@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "查询用户角色选项")
    @GetMapping("/admin/users/role")
    public Result<List<UserRoleDTO>> listUserRole(){
        return new Result<>(true, StatusConst.OK,"查询成功",roleService.listUserRoles());
    }

    @ApiOperation(value = "查询角色列表")
    @GetMapping("/admin/roles")
    public Result<PageDTO<RoleDTO>> listRoles(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",roleService.listRoles(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "保存或更新角色")
    @PostMapping("/admin/role")
    public Result listRoles(@Valid @RequestBody RoleVO roleVO){
        roleService.saveOrUpdateRole(roleVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除角色")
    @DeleteMapping("/admin/roles")
    public Result deleteRoles(@Valid @RequestBody List<Integer> roleIdList){
        roleService.deleteRoles(roleIdList);
        return new Result(true,StatusConst.OK,"操作成功");
    }
}
