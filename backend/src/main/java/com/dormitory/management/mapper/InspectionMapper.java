package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.Inspection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卫生检查Mapper接口
 */
@Mapper
public interface InspectionMapper extends BaseMapper<Inspection> {
}