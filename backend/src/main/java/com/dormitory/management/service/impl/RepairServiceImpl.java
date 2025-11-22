package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.RepairDTO;
import com.dormitory.management.dto.RepairHandleDTO;
import com.dormitory.management.dto.RepairPageDTO;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.Repair;
import com.dormitory.management.entity.Student;
import com.dormitory.management.enums.RepairPriorityEnum;
import com.dormitory.management.enums.RepairStatusEnum;
import com.dormitory.management.enums.RepairTypeEnum;
import com.dormitory.management.mapper.RepairMapper;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.service.RepairService;
import com.dormitory.management.service.StudentService;
import com.dormitory.management.vo.RepairVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报修服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    private final StudentService studentService;
    private final DormitoryService dormitoryService;

    @Override
    public IPage<RepairVO> getRepairPage(RepairPageDTO params) {
        // 构建查询条件
        LambdaQueryWrapper<Repair> queryWrapper = new LambdaQueryWrapper<Repair>()
                .eq(params.getRoomId() != null, Repair::getRoomId, params.getRoomId())
                .like(params.getRoomNo() != null && !params.getRoomNo().trim().isEmpty(), Repair::getRoomNo, params.getRoomNo())
                .eq(params.getStudentId() != null, Repair::getStudentId, params.getStudentId())
                .like(params.getStudentName() != null && !params.getStudentName().trim().isEmpty(), Repair::getStudentName, params.getStudentName())
                .eq(params.getType() != null && !params.getType().trim().isEmpty(), Repair::getType, params.getType())
                .eq(params.getStatus() != null, Repair::getStatus, params.getStatus())
                .eq(params.getPriority() != null, Repair::getPriority, params.getPriority())
                .ge(params.getStartDate() != null && !params.getStartDate().trim().isEmpty(), Repair::getCreateTime, params.getStartDate())
                .le(params.getEndDate() != null && !params.getEndDate().trim().isEmpty(), Repair::getCreateTime, params.getEndDate() + " 23:59:59")
                .orderByDesc(Repair::getCreateTime);

        // 执行分页查询
        Page<Repair> page = new Page<>(params.getCurrent(), params.getSize());
        IPage<Repair> repairPage = baseMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        Page<RepairVO> voPage = new Page<>(repairPage.getCurrent(), repairPage.getSize(), repairPage.getTotal());
        List<RepairVO> voList = repairPage.getRecords().stream()
                .map(this::convertToVO)
                .peek(this::setRepairDisplayText)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public RepairVO getRepairById(Long id) {
        Repair repair = getById(id);
        if (repair == null) {
            return null;
        }

        RepairVO repairVO = new RepairVO();
        BeanUtils.copyProperties(repair, repairVO);
        setRepairDisplayText(repairVO);
        // 处理图片列表
        if (StringUtils.hasText(repair.getImages())) {
            repairVO.setImageList(Arrays.asList(repair.getImages().split(",")));
        }
        return repairVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRepair(RepairDTO repairDTO) {
        // 验证学生是否存在
        Student student = studentService.getById(repairDTO.getStudentId());
        if (student == null) {
            throw new RuntimeException("报修学生不存在");
        }

        // 验证宿舍是否存在
        Dormitory dormitory = dormitoryService.getById(repairDTO.getRoomId());
        if (dormitory == null) {
            throw new RuntimeException("报修宿舍不存在");
        }

        Repair repair = new Repair();
        BeanUtils.copyProperties(repairDTO, repair);

        // 设置宿舍相关信息
        repair.setRoomNo(dormitory.getRoomNo());
        repair.setBuildingName(dormitory.getBuildingName());
        repair.setStudentName(student.getName());

        // 设置默认值
        repair.setStatus(RepairStatusEnum.PENDING.getCode());
        if (repair.getPriority() == null) {
            repair.setPriority(RepairPriorityEnum.MEDIUM.getCode());
        }
        repair.setReportTime(LocalDateTime.now());

        // 处理图片
        if (repairDTO.getImages() != null && !repairDTO.getImages().isEmpty()) {
            repair.setImages(String.join(",", repairDTO.getImages()));
        }

        return save(repair);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRepair(RepairHandleDTO handleDTO) {
        Repair repair = getById(handleDTO.getId());
        if (repair == null) {
            throw new RuntimeException("报修记录不存在");
        }

        // 更新状态
        repair.setStatus(handleDTO.getStatus());
        repair.setHandleRemark(handleDTO.getHandleRemark());

        // 设置处理人信息
        repair.setHandlerId(UserContext.getCurrentUserId());
        repair.setHandlerName(UserContext.getCurrentRealName());

        // 根据状态设置时间
        if (handleDTO.getStatus().equals(RepairStatusEnum.PROCESSING.getCode())) {
            repair.setHandleTime(LocalDateTime.now());
        } else if (handleDTO.getStatus().equals(RepairStatusEnum.COMPLETED.getCode())) {
            if (repair.getHandleTime() == null) {
                repair.setHandleTime(LocalDateTime.now());
            }
            repair.setCompleteTime(LocalDateTime.now());
        }

        return updateById(repair);
    }

    @Override
    public boolean deleteRepair(Long id) {
        return removeById(id);
    }

    /**
     * 设置报修显示文本
     */
    private void setRepairDisplayText(RepairVO repairVO) {
        repairVO.setTypeText(RepairTypeEnum.getDescriptionByCode(repairVO.getType()));
        repairVO.setStatusText(RepairStatusEnum.getDescriptionByCode(repairVO.getStatus()));
        repairVO.setPriorityText(RepairPriorityEnum.getDescriptionByCode(repairVO.getPriority()));
    }

    /**
     * 将Repair实体转换为RepairVO
     */
    private RepairVO convertToVO(Repair repair) {
        RepairVO repairVO = new RepairVO();
        BeanUtils.copyProperties(repair, repairVO);

        // 处理图片列表
        if (StringUtils.hasText(repair.getImages())) {
            repairVO.setImageList(Arrays.asList(repair.getImages().split(",")));
        }

        return repairVO;
    }
}