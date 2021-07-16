package com.siegfried.blog.controller;

import com.siegfried.blog.annotation.OptLog;
import com.siegfried.blog.constant.StatusConst;
import com.siegfried.blog.dto.ArticlePreviewListDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.TagDTO;
import com.siegfried.blog.entity.Tag;
import com.siegfried.blog.service.ArticleService;
import com.siegfried.blog.service.TagService;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.Result;
import com.siegfried.blog.vo.TagVO;
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
@Api(tags = "标签模块")
@RestController
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation("查看标签列表")
    @GetMapping("/tags")
    public Result<PageDTO<TagDTO>> listTags(){
        return new Result<>(true, StatusConst.OK,"查询成功",tagService.listTags());
    }

    @ApiOperation("查看标签对应的文章列表")
    @GetMapping("/tags/{tagId}")
    public Result<ArticlePreviewListDTO> listArticlesByTagId(@PathVariable("tagId") Integer tagId,Integer current){
        ConditionVO condition = ConditionVO.builder()
                .tagId(tagId)
                .current(current)
                .build();
        return new Result<>(true,StatusConst.OK,"查询成功",articleService.listArticlesByCondition(condition));
    }


    //------------后台接口-----------------

    @ApiOperation(value = "查看后台标签列表")
    @GetMapping("/admin/tags")
    public Result<PageDTO<Tag>> listTagBackDTO(ConditionVO conditionVO){
        return new Result<>(true,StatusConst.OK,"查询成功",tagService.listTagBackDTO(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "添加或修改标签")
    @PostMapping("/admin/tags")
    public Result saveOrUpdateTag(@Valid @RequestBody TagVO tagVO){
        tagService.saveOrUpdateTag(tagVO);
        return new Result(true,StatusConst.OK,"操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除标签")
    @DeleteMapping("/admin/tags")
    public Result deleteTag(@Valid @RequestBody List<Integer> tagIdList){
        tagService.deleteTag(tagIdList);
        return new Result(true,StatusConst.OK,"删除成功");
    }
}
