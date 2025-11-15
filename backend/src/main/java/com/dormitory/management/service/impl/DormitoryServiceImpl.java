package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.BedInfoDTO;
import com.dormitory.management.dto.DormitoryDTO;
import com.dormitory.management.dto.DormitoryPageDTO;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.DormBuilding;
import com.dormitory.management.entity.UserInfo;
import com.dormitory.management.mapper.DormBuildingMapper;
import com.dormitory.management.mapper.DormitoryMapper;
import com.dormitory.management.service.BedService;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.vo.DormitoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 宿舍Service实现类
 */
@Service
@RequiredArgsConstructor
public class DormitoryServiceImpl extends ServiceImpl<DormitoryMapper, Dormitory> implements DormitoryService {

    private final BedService bedService;

    @Override
    public Page<DormitoryVO> getDormitoryPage(DormitoryPageDTO dto) {
        // 使用MyBatis-Plus的LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<Dormitory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dormitory::getDeleted, 0);



        if (StringUtils.hasText(dto.getRoomNo())) {
            queryWrapper.like(Dormitory::getRoomNo, dto.getRoomNo());
        }

        if (Objects.nonNull(dto.getFloorNumber())) {
            queryWrapper.eq(Dormitory::getFloorNumber, dto.getFloorNumber());
        }


        if (Objects.nonNull(dto.getStatus())) {
            queryWrapper.eq(Dormitory::getStatus, dto.getStatus());
        }

        // 查询宿舍列表
        Page<Dormitory> dormitoryPage = this.page(new Page<>(dto.getCurrent(), dto.getSize()), queryWrapper);

        // 转换为VO对象
        List<DormitoryVO> dormitoryVOList = dormitoryPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<DormitoryVO> result = new Page<>(dto.getCurrent(), dto.getSize(), dormitoryPage.getTotal());
        result.setRecords(dormitoryVOList);

        return result;
    }

    @Override
    public DormitoryVO getDormitoryById(Long id) {
        Dormitory dormitory = this.getById(id);
        if (dormitory == null) {
            return null;
        }

        DormitoryVO dormitoryVO = new DormitoryVO();
        BeanUtils.copyProperties(dormitory, dormitoryVO);



        // 获取床位详细信息
        List<BedInfoDTO> bedList = bedService.getBedsByDormitoryId(id);
        dormitoryVO.setBedInfos(bedList);

        // 设置状态文本
        dormitoryVO.setStatusText(getStatusText(dormitory.getStatus()));

        return dormitoryVO;
    }

    @Override
    public List<DormitoryVO> getAvailableDormitories(Long buildingId) {
        // 使用MyBatis-Plus查询可用宿舍
        LambdaQueryWrapper<Dormitory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dormitory::getStatus, 1)
                   .eq(Dormitory::getDeleted, 0);





        List<Dormitory> dormitories = this.list(queryWrapper);
        // 过滤出有可用床位的宿舍
        return dormitories.stream()
//                .filter(dorm -> bedService.getAvailableBedCount(dorm.getId()) > 0)
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDormitory(DormitoryDTO dormitoryDTO) {
        // 检查房间号在同一楼栋是否已存在
        LambdaQueryWrapper<Dormitory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Dormitory::getRoomNo, dormitoryDTO.getRoomNo())
                .ne(Objects.nonNull(dormitoryDTO.getId()), Dormitory::getId, dormitoryDTO.getId())
                .eq(Dormitory::getDeleted, 0);

        Dormitory existing = this.getOne(queryWrapper);
        if (existing != null) {
            throw new RuntimeException("该楼栋中已存在相同房间号的宿舍");
        }
        if (Objects.nonNull(dormitoryDTO.getId())){
            // 校验是否已入住,已入住则不可编辑
            Dormitory byId = getById(dormitoryDTO.getId());
            if (byId.getOccupiedBeds()>=1){
                throw new RuntimeException("有学生入住不可编辑");
            }
        }

        UserInfo currentUser = UserContext.getCurrentUser();
        Dormitory dormitory = new Dormitory();
        BeanUtils.copyProperties(dormitoryDTO, dormitory);

        // 初始状态设为可用
        dormitory.setStatus(1);
        int size = dormitoryDTO.getBedInfos().size();
        // 设置总床位
        dormitory.setTotalBeds(size);
        dormitory.setAvailableBeds(size);
        dormitory.setOccupiedBeds(0); // 新宿舍初始占用数为0
        dormitory.setCreateTime(LocalDateTime.now());
        dormitory.setUpdateTime(LocalDateTime.now());
        dormitory.setCreateBy(currentUser != null ? currentUser.getRealName() : "system");
        dormitory.setUpdateBy(currentUser != null ? currentUser.getRealName() : "system");
        dormitory.setDeleted(0);

        // 保存宿舍信息
        boolean result = this.saveOrUpdate(dormitory);

        // 创建默认床位
        bedService.createDefaultBeds(dormitoryDTO.getBedInfos(),dormitory.getId());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDormitory(Long id) {
        Dormitory dormitory = this.getById(id);
        if (dormitory == null) {
            throw new RuntimeException("宿舍不存在");
        }

        // 检查是否有学生入住
        if (dormitory.getOccupiedBeds() > 0) {
            throw new RuntimeException("该宿舍还有学生入住，无法删除");
        }

        return this.removeById(id);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean updateDormitoryStatus(String id, Integer status, String updateBy) {
//        Dormitory dormitory = this.getById(id);
//        if (dormitory == null) {
//            throw new RuntimeException("宿舍不存在");
//        }
//
//        // 获取实际的床位信息
//        Integer occupiedBeds = bedService.getOccupiedBedCount(id);
//        Integer availableBeds = bedService.getAvailableBedCount(id);
//
//        // 如果设置为已满状态，检查实际入住人数
//        if (status == 0 && availableBeds > 0) {
//            throw new RuntimeException("该宿舍还有空床位，不能设置为已满状态");
//        }
//
//        dormitory.setStatus(status);
//        dormitory.setUpdateTime(LocalDateTime.now());
//        dormitory.setUpdateBy(updateBy);
//
//        return this.updateById(dormitory);
//    }
//
//    @Override
//    public Map<String, Object> getDormitoryBeds(String dormitoryId) {
//        // 使用MyBatis-Plus查询宿舍信息
//        Dormitory dormitory = this.getById(dormitoryId);
//        if (dormitory == null || dormitory.getDeleted() == 1) {
//            throw new RuntimeException("宿舍不存在");
//        }
//
//        // 从床位表获取实际床位信息
//        Integer occupiedBeds = bedService.getOccupiedBedCount(dormitoryId);
//        Integer availableBeds = bedService.getAvailableBedCount(dormitoryId);
//        Integer totalBeds = occupiedBeds + availableBeds;
//
//        // 构造床位信息
//        Map<String, Object> bedInfo = new HashMap<>();
//        bedInfo.put("bedCount", totalBeds);
//        bedInfo.put("occupiedBeds", occupiedBeds);
//        bedInfo.put("availableBeds", availableBeds);
//        bedInfo.put("bedList", bedService.getBedsByDormitoryId(dormitoryId));
//
//        return bedInfo;
//    }

    /**
     * 将Dormitory实体转换为DormitoryVO
     *
     * @param dormitory 宿舍实体
     * @return 宿舍VO对象
     */
    private DormitoryVO convertToVO(Dormitory dormitory) {
        DormitoryVO dormitoryVO = new DormitoryVO();
        BeanUtils.copyProperties(dormitory, dormitoryVO);

        // 设置状态文本
        dormitoryVO.setStatusText(getStatusText(dormitory.getStatus()));

        return dormitoryVO;
    }

    /**
     * 获取状态文本
     *
     * @param status 状态值
     * @return 状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "已满";
            case 1:
                return "可用";
            case 2:
                return "维护中";
            default:
                return "未知";
        }
    }

    @Override
    public List<DormitoryVO> getAllDormitories() {
        // 查询所有未删除的宿舍
        List<Dormitory> dormitories = this.list(new LambdaQueryWrapper<Dormitory>()
                .eq(Dormitory::getDeleted, 0)
                .orderByAsc(Dormitory::getBuildingName)
                .orderByAsc(Dormitory::getRoomNo));

        return dormitories.stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
    }
}