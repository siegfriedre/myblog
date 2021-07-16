package com.siegfried.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siegfried.blog.dto.UserBackDTO;
import com.siegfried.blog.entity.UserAuth;
import com.siegfried.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zy_zhu
 * @date 2021/5/23 14:50
 */
@Repository
public interface UserAuthDao extends BaseMapper<UserAuth> {

    /**
     * 查询后台用户列表
     * @param condition 条件
     * @return 用户集合
     */
    List<UserBackDTO> listUsers(@Param("condition") ConditionVO condition);//别把param的包导错了

    /**
     * 查询后台用户数量
     * @param condition 条件
     * @return 用户数量
     */
    Integer countUser(@Param("condition") ConditionVO condition);
}
