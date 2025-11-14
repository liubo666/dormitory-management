package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.Dormitory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宿舍Mapper接口
 * 使用MyBatis-Plus的BaseMapper提供基本CRUD操作
 */
@Mapper
public interface DormitoryMapper extends BaseMapper<Dormitory> {
}