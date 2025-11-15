package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访客登记Mapper接口
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {
}