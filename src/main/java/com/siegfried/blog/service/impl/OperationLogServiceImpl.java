package com.siegfried.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siegfried.blog.dao.OperationLogDao;
import com.siegfried.blog.dto.OperationLogDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.entity.OperationLog;
import com.siegfried.blog.service.OperationLogService;
import com.siegfried.blog.utils.BeanCopyUtil;
import com.siegfried.blog.vo.ConditionVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Created by zy_zhu on 2021/5/10.
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogDao, OperationLog> implements OperationLogService {
    @Override
    public PageDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO) {
        Page<OperationLog> page = new Page(conditionVO.getCurrent(),conditionVO.getSize());
        // 查询日志列表
        Page<OperationLog> operationLogPage = this.page(page,new LambdaQueryWrapper<OperationLog>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),OperationLog::getOptModule,conditionVO.getKeywords())
                .or()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),OperationLog::getOptDesc,conditionVO.getKeywords())
                .gt(Objects.nonNull(conditionVO.getStartTime()),OperationLog::getCreateTime,conditionVO.getStartTime())
                .lt(Objects.nonNull(conditionVO.getEndTime()),OperationLog::getCreateTime,conditionVO.getEndTime())
                .orderByDesc(OperationLog::getId));
        List<OperationLogDTO> operationLogDTOList = BeanCopyUtil.copyList(operationLogPage.getRecords(),OperationLogDTO.class);
        return new PageDTO<>(operationLogDTOList,(int)operationLogPage.getTotal());
    }
}
