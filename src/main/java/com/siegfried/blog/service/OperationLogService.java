package com.siegfried.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siegfried.blog.dto.OperationLogDTO;
import com.siegfried.blog.dto.PageDTO;
import com.siegfried.blog.entity.OperationLog;
import com.siegfried.blog.vo.ConditionVO;

/**
 * Created by zy_zhu on 2021/5/10.
 */
public interface OperationLogService extends IService<OperationLog> {
    /**
     * 查询日志列表
     *
     * @param conditionVO 条件
     * @return 日志列表
     */
    PageDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}
