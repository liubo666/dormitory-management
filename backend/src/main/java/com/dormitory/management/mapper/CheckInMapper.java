package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入住分配Mapper接口
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}