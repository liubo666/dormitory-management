package com.dormitory.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.RepairDTO;
import com.dormitory.management.dto.RepairHandleDTO;
import com.dormitory.management.dto.RepairPageDTO;
import com.dormitory.management.entity.Repair;
import com.dormitory.management.vo.RepairVO;

/**
 * 报修服务接口
 */
public interface RepairService extends IService<Repair> {

    /**
     * 分页查询报修列表
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<RepairVO> getRepairPage(RepairPageDTO params);

    /**
     * 根据ID查询报修详情
     * @param id 报修ID
     * @return 报修详情
     */
    RepairVO getRepairById(Long id);

    /**
     * 新增报修
     * @param repairDTO 报修信息
     * @return 是否成功
     */
    boolean addRepair(RepairDTO repairDTO);

    /**
     * 处理报修
     * @param handleDTO 处理信息
     * @return 是否成功
     */
    boolean handleRepair(RepairHandleDTO handleDTO);

    /**
     * 删除报修
     * @param id 报修ID
     * @return 是否成功
     */
    boolean deleteRepair(Long id);
}