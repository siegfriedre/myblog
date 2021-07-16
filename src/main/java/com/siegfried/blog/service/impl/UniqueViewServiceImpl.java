package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.UniqueViewDao;
import com.siegfried.blog.dto.UniqueViewDTO;
import com.siegfried.blog.entity.UniqueView;
import com.siegfried.blog.service.UniqueViewService;
import com.siegfried.blog.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by zy_zhu on 2021/5/7.
 */
@Service
@Slf4j
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewDao, UniqueView> implements UniqueViewService {

    @Autowired
    UniqueViewDao uniqueViewDao;

    @Override
    public List<UniqueViewDTO> listUniqueViews() {
//        String startTime = DateUtil.getMinTime(DateUtil.getSomeDay(new Date(),-7));
//        String endTime = DateUtil.getMaxTime(new Date());
        String startTime = DateUtil.getStartTime(new Date(),-7);
        String endTime = DateUtil.getNowTime(new Date());
   //     log.info("startTime:{}-----------  endTime{}--------",startTime,endTime);
        return uniqueViewDao.listUniqueViews(startTime,endTime);
    }
}
