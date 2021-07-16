package com.siegfried.blog.service;

import com.siegfried.blog.dto.FriendLinkBackDTO;
import com.siegfried.blog.dto.FriendLinkDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.FriendLinkVO;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/3.
 */
public interface FriendLinkService{
    /**
     * 查询友情列表
     * @return 友情列表
     */
    List<FriendLinkDTO> listFriendLinks();

    /**
     * 后台查询友链列表
     * @param condition 条件
     * @return 后台友链列表
     */
    PageDTO<FriendLinkBackDTO> listFriendLinkDTO (ConditionVO condition);

    /**
     * 新增或修改友链
     * @param friendLinkVO
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

    /**
     * 批量删除友链
     * @param linkIdList 要删除的友链的id列表
     */
    void removeByIds(List<Integer> linkIdList);
}
