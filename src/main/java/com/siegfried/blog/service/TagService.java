package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.dto.TagDTO;
import com.siegfried.blog.entity.Tag;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.TagVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/3.
 */
public interface TagService extends IService<Tag> {
    /**
     * 查询tag
     * @return tagDTO的page对象
     */
    PageDTO<TagDTO> listTags();

    /**
     * 后台查询tag
     * @param condition 条件
     * @return tag的page对象
     */
    PageDTO<Tag> listTagBackDTO(ConditionVO condition);

    /**
     * 新增或修改tag
     * @param tagVO
     */
    void saveOrUpdateTag(TagVO tagVO);

    /**
     * 批量删除tag
     * @param tagIdList tagId列表
     */
    void deleteTag(List<Integer> tagIdList);
}
