package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.BlogHomeInfoDTO;
import com.siegfried.blog.service.BlogInfoService;
import com.siegfried.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.siegfried.blog.constant.OptTypeConst.UPDATE;

/**
 * Created by zy_zhu on 2021/5/3.
 */
@Api(tags = "关于我，博客信息")
@RestController
public class BlogInfoController {

    @Autowired
    private BlogInfoService blogInfoService;

    @ApiOperation("主页的博客信息")
    @GetMapping("/")
    public Result<BlogHomeInfoDTO> getBlogHomeInfo(){
        return new Result<>(true,StatusConst.OK,"查询成功",blogInfoService.getBlogInfo());
    }

    @ApiOperation("关于我")
    @GetMapping("/about")
    public Result<String> getAbout(){
        return new Result<>(true, StatusConst.OK,"查询成功",blogInfoService.getAbout());
    }

    //下面是后台api

    @ApiOperation("查看后台信息")
    @GetMapping("/admin")
    public Result<BlogHomeInfoDTO> getBlogBackInfo(){
        return new Result<>(true,StatusConst.OK,"查询成功",blogInfoService.getBlogBackInfo());
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改关于我信息")
    @PutMapping("/admin/about")
    public Result updateAbout(String aboutContent){
        blogInfoService.updateAbout(aboutContent);
        return new Result(true,StatusConst.OK,"修改成功");
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改公告")
    @PutMapping("/admin/notice")
    public Result updateNotice(String notice){
        blogInfoService.updateNotice(notice);
        return new Result(true,StatusConst.OK,"修改成功");
    }

    @ApiOperation(value = "查看公告")
    @GetMapping("/admin/notice")
    public Result<String> getNotice(){
        return new Result(true,StatusConst.OK,"查看成功",blogInfoService.getNotice());
    }
}
