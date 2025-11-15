package com.dormitory.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.dto.RepairPageDTO;
import com.dormitory.management.entity.Repair;
import com.dormitory.management.vo.RepairVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报修Mapper接口
 */
@Mapper
public interface RepairMapper extends BaseMapper<Repair> {

    /**
     * 分页查询报修列表
     * @param page 分页对象
     * @param params 查询参数
     * @return 报修列表
     */
    IPage<RepairVO> selectRepairPage(Page<RepairVO> page, @Param("params") RepairPageDTO params);

    /**
     * 根据ID查询报修详情
     * @param id 报修ID
     * @return 报修详情
     */
    RepairVO selectRepairById(@Param("id") Long id);
}