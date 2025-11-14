package com.dormitory.management.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.dto.BedDTO;
import com.dormitory.management.dto.BedInfoDTO;
import com.dormitory.management.entity.Bed;
import com.dormitory.management.mapper.BedMapper;
import com.dormitory.management.service.BedService;
import com.dormitory.management.vo.BedVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 床位Service实现类
 */
@Service
@RequiredArgsConstructor
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {

    @Override
    public List<BedInfoDTO> getBedsByDormitoryId(String dormitoryId) {
        LambdaQueryWrapper<Bed> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bed::getDormitoryId, dormitoryId)
                   .orderByAsc(Bed::getBedNo);

        List<Bed> beds = this.list(queryWrapper);
        return beds.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDefaultBeds(List<BedInfoDTO> list, Long dormitoryId) {
        // 先删除现有的床位
        LambdaQueryWrapper<Bed> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Bed::getDormitoryId, dormitoryId);
        this.remove(deleteWrapper);

        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Bed> beds = list.stream().map(v -> {
            Bed bed = BeanUtil.copyProperties(v, Bed.class);
            bed.setDormitoryId(dormitoryId);
            return bed;
        }).collect(Collectors.toList());

       this.saveBatch(beds);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void createDefaultBeds(Long dormitoryId, Integer bedCount, String createBy) {
//        // 先删除现有的床位（编辑时）
//        LambdaQueryWrapper<Bed> deleteWrapper = new LambdaQueryWrapper<>();
//        deleteWrapper.eq(Bed::getDormitoryId, dormitoryId);
//        this.remove(deleteWrapper);
//
//        if (bedCount == null || bedCount <= 0) {
//            return;
//        }
//
//        // 创建床位列表
//        List<Bed> beds = new ArrayList<>();
//        for (int i = 1; i <= bedCount; i++) {
//            Bed bed = new Bed();
//            bed.setDormitoryId(dormitoryId);
//            bed.setBedNo("床位" + i);
//            bed.setStatus(1); // 1: 可用
//            beds.add(bed);
//        }
//
//        this.saveBatch(beds);
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean updateBed(BedDTO bedDTO, String updateBy) {
//        Bed bed = this.getById(bedDTO.getId());
//        if (bed == null) {
//            throw new RuntimeException("床位不存在");
//        }
//
//        BeanUtils.copyProperties(bedDTO, bed, "id", "dormitoryId", "createTime", "createBy");
//
//        return this.updateById(bed);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean deleteBed(String id) {
//        // 检查床位是否被占用
//        Bed bed = this.getById(id);
//        if (bed == null) {
//            throw new RuntimeException("床位不存在");
//        }
//
//        if (bed.getStatus() == 1) {
//            throw new RuntimeException("床位已被占用，无法删除");
//        }
//
//        return this.removeById(id);
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean updateBedStatus(String id, Integer status, String updateBy) {
//        Bed bed = this.getById(id);
//        if (bed == null) {
//            throw new RuntimeException("床位不存在");
//        }
//
//        bed.setStatus(status);
//
//
//        return this.updateById(bed);
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean assignBedToStudent(String bedId, String updateBy) {
//        Bed bed = this.getById(bedId);
//        if (bed == null) {
//            throw new RuntimeException("床位不存在");
//        }
//
//        if (bed.getStatus() != 0) {
//            throw new RuntimeException("床位不可用");
//        }
//
//        bed.setStatus(1); // 已占用
//
//
//        return this.updateById(bed);
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean releaseBed(Long bedId, String updateBy) {
//        Bed bed = this.getById(bedId);
//        if (bed == null) {
//            throw new RuntimeException("床位不存在");
//        }
//
//        bed.setStatus(0); // 可用
//
//
//        return this.updateById(bed);
//    }
//
//    @Override
//    public Integer getAvailableBedCount(Long dormitoryId) {
//        LambdaQueryWrapper<Bed> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Bed::getDormitoryId, dormitoryId)
//                   .eq(Bed::getStatus, 0);
//
//        return Math.toIntExact(this.count(queryWrapper));
//    }
//
//    @Override
//    public Integer getOccupiedBedCount(Long dormitoryId) {
//        LambdaQueryWrapper<Bed> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Bed::getDormitoryId, dormitoryId)
//                   .eq(Bed::getStatus, 1);
//
//        return Math.toIntExact(this.count(queryWrapper));
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean batchSaveBeds(List<Bed> beds) {
//        return this.saveBatch(beds);
//    }

    private BedInfoDTO convertToVO(Bed bed) {
        BedInfoDTO vo = new BedInfoDTO();
        BeanUtils.copyProperties(bed, vo);

        // 设置状态文本
        switch (bed.getStatus()) {
            case 0:
                vo.setStatusText("可用");
                vo.setIsOccupied(false);
                break;
            case 1:
                vo.setStatusText("已占用");
                vo.setIsOccupied(true);
                break;
            case 2:
                vo.setStatusText("维修中");
                vo.setIsOccupied(true);
                break;
            default:
                vo.setStatusText("未知");
                vo.setIsOccupied(false);
        }

        return vo;
    }
}