package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.dto.UniqueViewDTO;
import com.siegfried.blog.entity.UniqueView;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zy_zhu on 2021/5/7.
 */
@Repository
public interface UniqueViewDao extends BaseMapper<UniqueView> {
    /**
     * 获取一周的访问量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 访问量列表
     */
    List<UniqueViewDTO> listUniqueViews(@Param("startTime") String startTime,@Param("endTime") String endTime);
}
