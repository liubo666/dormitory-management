package com.dormitory.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.DormitoryDTO;
import com.dormitory.management.dto.DormitoryPageDTO;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.vo.DormitoryVO;

import java.util.List;

/**
 * 宿舍Service接口
 */
public interface DormitoryService extends IService<Dormitory> {

    /**
     * 分页查询宿舍列表

     * @return 分页结果
     */
    Page<DormitoryVO> getDormitoryPage(DormitoryPageDTO  dto);

    /**
     * 根据ID获取宿舍详情
     *
     * @param id 宿舍ID
     * @return 宿舍详情
     */
    DormitoryVO getDormitoryById(Long id);

    /**
     * 获取可用宿舍列表
     *
     * @param buildingId 楼栋ID（可选）
     * @return 可用宿舍列表
     */
    List<DormitoryVO> getAvailableDormitories(Long buildingId);

    /**
     * 新增宿舍
     *
     * @param dormitoryDTO 宿舍信息
     * @return 是否成功
     */
    boolean createDormitory(DormitoryDTO dormitoryDTO);

    /**
     * 删除宿舍
     *
     * @param id 宿舍ID
     * @return 是否成功
     */
    boolean deleteDormitory(Long id);

    /**
     * 获取所有宿舍选项列表
     *
     * @return 宿舍列表
     */
    List<DormitoryVO> getAllDormitories();


}