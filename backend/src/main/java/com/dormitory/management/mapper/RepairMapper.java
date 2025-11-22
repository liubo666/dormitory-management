package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.management.entity.Repair;
import com.dormitory.management.vo.RepairVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报修Mapper接口
 */
@Mapper
public interface RepairMapper extends BaseMapper<Repair> {


}