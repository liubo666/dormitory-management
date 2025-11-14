package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口
 * 使用MyBatis-Plus的BaseMapper提供基本CRUD操作
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}