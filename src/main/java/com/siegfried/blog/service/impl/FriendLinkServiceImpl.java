package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.FriendLinkDao;
import com.siegfried.blog.dto.FriendLinkBackDTO;
import com.siegfried.blog.dto.FriendLinkDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.entity.FriendLink;
import com.siegfried.blog.service.FriendLinkService;
import com.siegfried.blog.utils.BeanCopyUtil;
import com.siegfried.blog.vo.ConditionVO;
import com.siegfried.blog.vo.FriendLinkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by zy_zhu on 2021/5/3.
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkDao, FriendLink> implements FriendLinkService {

    @Autowired
    FriendLinkDao friendLinkDao;

    @Override
    public List<FriendLinkDTO> listFriendLinks() {
        List<FriendLink> friendLinkList =  friendLinkDao.selectList(new LambdaQueryWrapper<FriendLink>()
                .select(FriendLink::getId,FriendLink::getLinkName,FriendLink::getLinkAvatar,FriendLink::getLinkAddress,FriendLink::getLinkIntro));
        return BeanCopyUtil.copyList(friendLinkList,FriendLinkDTO.class);
    }

    @Override
    public PageDTO<FriendLinkBackDTO> listFriendLinkDTO(ConditionVO condition) {
        Page<FriendLink> page = new Page<>(condition.getCurrent(),condition.getSize());
        Page<FriendLink> friendLinkPage = friendLinkDao.selectPage(page,new LambdaQueryWrapper<FriendLink>()
                .select(FriendLink::getId,FriendLink::getLinkName,FriendLink::getLinkAvatar,FriendLink::getLinkAddress,FriendLink::getLinkIntro, FriendLink::getCreateTime)
                .like(StringUtils.isNotBlank(condition.getKeywords()),FriendLink::getLinkName,condition.getKeywords()));
        // 转换DTO
        List<FriendLinkBackDTO> friendLinkBackDTOList = BeanCopyUtil.copyList(friendLinkPage.getRecords(),FriendLinkBackDTO.class);
        return new PageDTO<>(friendLinkBackDTOList,(int) friendLinkPage.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO) {
        FriendLink friendLink = FriendLink.builder()
                .id(friendLinkVO.getId())
                .linkName(friendLinkVO.getLinkName())
                .linkAvatar(friendLinkVO.getLinkAvatar())
                .linkAddress(friendLinkVO.getLinkAddress())
                .linkIntro(friendLinkVO.getLinkIntro())
                .createTime(Objects.isNull(friendLinkVO.getId()) ? new Date() : null)
                .build();
        this.saveOrUpdate(friendLink);
    }

    @Override
    public void removeByIds(List<Integer> linkIdList) {
        friendLinkDao.deleteBatchIds(linkIdList);
    }
}
