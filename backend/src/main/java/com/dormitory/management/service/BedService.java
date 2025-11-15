package com.dormitory.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.BedDTO;
import com.dormitory.management.dto.BedInfoDTO;
import com.dormitory.management.entity.Bed;
import com.dormitory.management.vo.BedVO;

import java.util.List;

/**
 * 床位Service接口
 */
public interface BedService extends IService<Bed> {

    /**
     * 根据宿舍ID获取床位列表
     */
    List<BedInfoDTO> getBedsByDormitoryId(Long dormitoryId);

    /**
     * 创建宿舍的默认床位
     */
    void createDefaultBeds(List<BedInfoDTO> dto,Long dormitoryId);

    /**
     * 根据床位数创建默认床位
     */
//    void createDefaultBeds(Long dormitoryId, Integer bedCount, String createBy);

//    /**
//     * 更新床位信息
//     */
//    boolean updateBed(BedDTO bedDTO, String updateBy);
//
//    /**
//     * 删除床位
//     */
//    boolean deleteBed(String id);

    /**
     * 更新床位状态
     */
//    boolean updateBedStatus(String id, Integer status, String updateBy);

//    /**
//     * 分配床位给学生
//     */
//    boolean assignBedToStudent(String bedId, String updateBy);
//
//    /**
//     * 释放床位
//     */
//    boolean releaseBed(String bedId, String updateBy);

//    /**
//     * 获取宿舍可用床位数
//     */
//    Integer getAvailableBedCount(Long dormitoryId);
//
//    /**
//     * 获取宿舍已占用床位数
//     */
//    Integer getOccupiedBedCount(Long dormitoryId);

//    /**
//     * 批量保存床位
//     */
//    boolean batchSaveBeds(List<Bed> beds);
}