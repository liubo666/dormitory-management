package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.Bed;
import org.apache.ibatis.annotations.Mapper;

/**
 * 床位Mapper接口
 */
@Mapper
public interface BedMapper extends BaseMapper<Bed> {
}