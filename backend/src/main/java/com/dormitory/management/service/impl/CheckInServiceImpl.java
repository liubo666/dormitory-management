package com.dormitory.management.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.dto.*;
import com.dormitory.management.entity.Bed;
import com.dormitory.management.entity.CheckIn;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.Student;
import com.dormitory.management.mapper.BedMapper;
import com.dormitory.management.mapper.CheckInMapper;
import com.dormitory.management.mapper.DormitoryMapper;
import com.dormitory.management.mapper.StudentMapper;
import com.dormitory.management.service.CheckInService;
import com.dormitory.management.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入住分配Service实现类
 */
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {

    private final StudentMapper studentMapper;
    private final DormitoryMapper dormitoryMapper;
    private final BedMapper bedMapper;

    @Override
    public Page<CheckInVO> getCheckInPage(Page<CheckInVO> page, CheckInPageDTO pageDTO) {
        // 使用MyBatis-Plus的LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<CheckIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckIn::getDeleted, 0);

        if (StringUtils.hasText(pageDTO.getStudentId())) {
            queryWrapper.eq(CheckIn::getStudentId, pageDTO.getStudentId());
        }

//        if (StringUtils.hasText(pageDTO.getDormitoryId())) {
//            queryWrapper.eq(CheckIn::getDormitoryId, pageDTO.getDormitoryId());
//        }
//
//        if (StringUtils.hasText(pageDTO.getBedNo())) {
//            queryWrapper.like(CheckIn::getBedNo, pageDTO.getBedNo());
//        }

        if (pageDTO.getStatus() != null) {
            queryWrapper.eq(CheckIn::getStatus, pageDTO.getStatus());
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(CheckIn::getCreateTime);

        // 查询入住分配记录
        Page<CheckIn> checkInPage = new Page<>(page.getCurrent(), page.getSize());
        Page<CheckIn> result = this.page(checkInPage, queryWrapper);

        // 转换为VO
        Page<CheckInVO> voPage = new Page<>(page.getCurrent(), page.getSize());
        voPage.setTotal(result.getTotal());

        List<CheckInVO> voList = result.getRecords().stream().map(this::convertToVO).toList();
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public CheckInVO getCheckInById(String id) {
        CheckIn checkIn = this.getById(id);
        if (checkIn == null) {
            return null;
        }
        return convertToVO(checkIn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitCheckInApplication(CheckInDTO checkInDTO, String createBy) {
        // 检查学生是否存在
        Student student = studentMapper.selectById(checkInDTO.getStudentId());
        if (student == null || student.getDeleted() == 1) {
            throw new RuntimeException("学生不存在");
        }

        // 检查学生是否已入住
        LambdaQueryWrapper<CheckIn> existingQuery = new LambdaQueryWrapper<>();
        existingQuery.eq(CheckIn::getStudentId, checkInDTO.getStudentId())
                   .eq(CheckIn::getStatus, 1)
                   .eq(CheckIn::getDeleted, 0);
        CheckIn existing = this.getOne(existingQuery);
        if (existing != null) {
            throw new RuntimeException("该学生已有入住记录");
        }

        // 检查是否有待审批的申请
        existingQuery.clear();
        existingQuery.eq(CheckIn::getStudentId, checkInDTO.getStudentId())
                   .eq(CheckIn::getStatus, 0)
                   .eq(CheckIn::getDeleted, 0);
        existing = this.getOne(existingQuery);
        if (existing != null) {
            throw new RuntimeException("该学生已有待审批的入住申请");
        }

        CheckIn checkIn = new CheckIn();
        BeanUtils.copyProperties(checkInDTO, checkIn);
        checkIn.setStatus(0); // 申请中
        checkIn.setCreateTime(LocalDateTime.now());
        checkIn.setUpdateTime(LocalDateTime.now());
        checkIn.setCreateBy(createBy);
        checkIn.setUpdateBy(createBy);
        checkIn.setDeleted(0);

        return this.save(checkIn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveCheckIn(String id, Integer status, String approvalRemark, String approver) {
        CheckIn checkIn = this.getById(id);
        if (checkIn == null) {
            throw new RuntimeException("入住申请不存在");
        }

        if (checkIn.getStatus() != 0) {
            throw new RuntimeException("只能审批申请中的记录");
        }

        checkIn.setStatus(status);
        checkIn.setApprovalRemark(approvalRemark);
        checkIn.setApprover(approver);
        checkIn.setApprovalTime(LocalDateTime.now());
        checkIn.setUpdateTime(LocalDateTime.now());
        checkIn.setUpdateBy(approver);

        boolean result = this.updateById(checkIn);

        // 如果审批通过，需要分配宿舍
        if (result && status == 1) {
            // 检查是否有宿舍和床位信息
//            if (StringUtils.hasText(checkIn.getDormitoryId()) && StringUtils.hasText(checkIn.getBedId())) {
//                // 更新床位状态为已占用
////                bedService.updateBedStatus(checkIn.getBedId(), 1, approver);
//
//                // 更新学生宿舍信息
//                Student student = new Student();
//                student.setId(checkIn.getStudentId());
//                student.setDormitoryId(checkIn.getDormitoryId());
//                student.setBedNo(checkIn.getBedNo());
//                student.setUpdateBy(approver);
//                student.setUpdateTime(LocalDateTime.now());
//                studentMapper.updateById(student);
//            }

            // 如果没有指定宿舍和床位，进入待分配状态
            // 这里可以根据业务需求进行处理
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignDormitory(String checkInId, String dormitoryId, String bedId, String bedNo, String updateBy) {
        CheckIn checkIn = this.getById(checkInId);
        if (checkIn == null) {
            throw new RuntimeException("入住记录不存在");
        }

        if (checkIn.getStatus() != 1) {
            throw new RuntimeException("只能为已入住的记录分配宿舍");
        }

        // 检查床位是否可用
        Bed bed = bedMapper.selectById(bedId);
        if (bed == null ) {
            throw new RuntimeException("床位不存在");
        }

        if (bed.getStatus() != 0) {
            throw new RuntimeException("床位不可用");
        }

        // 如果原来有宿舍分配，需要释放原床位
//        if (StringUtils.hasText(checkIn.getBedId()) && !checkIn.getBedId().equals(bedId)) {
////            bedService.updateBedStatus(checkIn.getBedId(), 0, updateBy);
//        }
//
//        // 更新入住记录
//        checkIn.setDormitoryId(dormitoryId);
//        checkIn.setBedId(bedId);
//        checkIn.setBedNo(bedNo);
        checkIn.setUpdateTime(LocalDateTime.now());
        checkIn.setUpdateBy(updateBy);

        boolean result = this.updateById(checkIn);

        // 更新床位状态
        if (result) {
//            bedService.updateBedStatus(bedId, 1, updateBy);

            // 更新学生宿舍信息
            Student student = new Student();
            student.setId(checkIn.getStudentId());
            student.setDormitoryId(dormitoryId);
//            student.setBedNo(bedNo);
            student.setUpdateBy(updateBy);
            student.setUpdateTime(LocalDateTime.now());
            studentMapper.updateById(student);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkout(String checkInId, String checkoutReason, String updateBy) {
        CheckIn checkIn = this.getById(checkInId);
        if (checkIn == null) {
            throw new RuntimeException("入住记录不存在");
        }

        if (checkIn.getStatus() != 1) {
            throw new RuntimeException("只能为已入住的记录办理退宿");
        }

//        // 释放床位
//        if (StringUtils.hasText(checkIn.getBedId())) {
////            bedService.updateBedStatus(checkIn.getBedId(), 0, updateBy);
//        }

        // 更新入住记录
        checkIn.setStatus(2); // 已退宿
//        checkIn.setActualCheckoutDate(LocalDateTime.now());
        checkIn.setCheckoutReason(checkoutReason);
        checkIn.setUpdateTime(LocalDateTime.now());
        checkIn.setUpdateBy(updateBy);

        boolean result = this.updateById(checkIn);

        // 清空学生宿舍信息
        if (result) {
            Student student = new Student();
            student.setId(checkIn.getStudentId());
            student.setDormitoryId(null);
//            student.setBedNo(null);
            student.setUpdateBy(updateBy);
            student.setUpdateTime(LocalDateTime.now());
            studentMapper.updateById(student);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelApplication(String checkInId, String reason, String updateBy) {
        CheckIn checkIn = this.getById(checkInId);
        if (checkIn == null) {
            throw new RuntimeException("入住申请不存在");
        }

        if (checkIn.getStatus() != 0) {
            throw new RuntimeException("只能取消申请中的记录");
        }

        checkIn.setStatus(3); // 已拒绝
        checkIn.setApprovalRemark("取消申请：" + reason);
        checkIn.setApprover(updateBy);
        checkIn.setApprovalTime(LocalDateTime.now());
        checkIn.setUpdateTime(LocalDateTime.now());
        checkIn.setUpdateBy(updateBy);

        return this.updateById(checkIn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchApprove(List<String> ids, Integer status, String approvalRemark, String approver) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (String id : ids) {
            try {
                boolean success = approveCheckIn(id, status, approvalRemark, approver);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("ID: " + id + " 审批失败");
                }
            } catch (Exception e) {
                failCount++;
                errorMessages.add("ID: " + id + " 审批失败: " + e.getMessage());
            }
        }

        result.put("totalCount", ids.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);

        return result;
    }

    @Override
    public Map<String, Object> getCheckInStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总入住记录数
        LambdaQueryWrapper<CheckIn> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(CheckIn::getDeleted, 0);
        long totalCount = this.count(totalQuery);
        statistics.put("totalCount", totalCount);

        // 申请中数量
        LambdaQueryWrapper<CheckIn> applyingQuery = new LambdaQueryWrapper<>();
        applyingQuery.eq(CheckIn::getStatus, 0).eq(CheckIn::getDeleted, 0);
        long applyingCount = this.count(applyingQuery);
        statistics.put("applyingCount", applyingCount);

        // 已入住数量
        LambdaQueryWrapper<CheckIn> checkedInQuery = new LambdaQueryWrapper<>();
        checkedInQuery.eq(CheckIn::getStatus, 1).eq(CheckIn::getDeleted, 0);
        long checkedInCount = this.count(checkedInQuery);
        statistics.put("checkedInCount", checkedInCount);

        // 已退宿数量
        LambdaQueryWrapper<CheckIn> checkedOutQuery = new LambdaQueryWrapper<>();
        checkedOutQuery.eq(CheckIn::getStatus, 2).eq(CheckIn::getDeleted, 0);
        long checkedOutCount = this.count(checkedOutQuery);
        statistics.put("checkedOutCount", checkedOutCount);

        // 已拒绝数量
        LambdaQueryWrapper<CheckIn> rejectedQuery = new LambdaQueryWrapper<>();
        rejectedQuery.eq(CheckIn::getStatus, 3).eq(CheckIn::getDeleted, 0);
        long rejectedCount = this.count(rejectedQuery);
        statistics.put("rejectedCount", rejectedCount);

        return statistics;
    }

    @Override
    public CheckInVO getCheckInByStudentId(String studentId) {
        LambdaQueryWrapper<CheckIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckIn::getStudentId, studentId)
                   .eq(CheckIn::getDeleted, 0)
                   .orderByDesc(CheckIn::getCreateTime)
                   .last("1");

        CheckIn checkIn = this.getOne(queryWrapper);
        return checkIn != null ? convertToVO(checkIn) : null;
    }

    @Override
    public List<CheckInVO> getCheckInsByDormitoryId(String dormitoryId) {
        LambdaQueryWrapper<CheckIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                   .eq(CheckIn::getStatus, 1) // 只查询已入住的
                   .eq(CheckIn::getDeleted, 0)
                   .orderByDesc(CheckIn::getCreateTime);

        List<CheckIn> checkIns = this.list(queryWrapper);
        return checkIns.stream().map(this::convertToVO).toList();
    }

    @Override
    public CheckInVO getCheckInByBedId(String bedId) {
        LambdaQueryWrapper<CheckIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckIn::getBedId, bedId)
                   .eq(CheckIn::getStatus, 1) // 只查询已入住的
                   .eq(CheckIn::getDeleted, 0);

        CheckIn checkIn = this.getOne(queryWrapper);
        return checkIn != null ? convertToVO(checkIn) : null;
    }

    private CheckInVO convertToVO(CheckIn checkIn) {
        CheckInVO vo = new CheckInVO();
        BeanUtils.copyProperties(checkIn, vo);

        // 设置状态文本
        switch (checkIn.getStatus()) {
            case 0:
                vo.setStatusText("申请中");
                break;
            case 1:
                vo.setStatusText("已入住");
                break;
            case 2:
                vo.setStatusText("已退宿");
                break;
            case 3:
                vo.setStatusText("已拒绝");
                break;
            default:
                vo.setStatusText("未知");
        }

        // 获取学生信息
        Student student = studentMapper.selectById(checkIn.getStudentId());
        if (student != null) {
            vo.setStudentNo(student.getStudentNo());
            vo.setStudentName(student.getName());
            vo.setStudentGender(student.getGender());
            vo.setStudentGenderText(student.getGender() == 1 ? "男" : "女");
            vo.setCollege(student.getCollege());
            vo.setMajor(student.getMajor());
            vo.setClassName(student.getClassName());
        }

//        // 获取宿舍信息
//        if (StringUtils.hasText(checkIn.getDormitoryId())) {
//            Dormitory dormitory = dormitoryMapper.selectById(checkIn.getDormitoryId());
//            if (dormitory != null) {
//                // 这里需要关联查询楼栋信息
//                vo.setRoomNo(dormitory.getRoomNo());
//            }
//        }

        return vo;
    }

    @Override
    public Page<AvailableStudentDTO> getAvailableStudents(CheckInResDTO dto) {

        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
         studentWrapper.eq(Student::getStatus, 1)
                .in(Student::getCheckInStatus, Arrays.asList(0,3,4));

        if (StringUtils.hasText(dto.getKeyword())) {
            studentWrapper.and(wrapper -> wrapper
                .like(Student::getName, dto.getKeyword())
                .or()
                .like(Student::getStudentNo, dto.getKeyword())
            );
        }
        Page<Student> studentPage = studentMapper.selectPage(new Page<>(dto.getPageIndex(), dto.getPageSize()), studentWrapper);

        Page<AvailableStudentDTO> voPage = new Page<>(dto.getPageIndex(), dto.getPageSize());
        voPage.setTotal(studentPage.getTotal());

        voPage.setRecords(BeanUtil.copyToList(studentPage.getRecords(),AvailableStudentDTO.class));
        return voPage;
    }

    @Override
    public List<AvailableBedDTO> getAvailableBeds(String  keyword) {
        List<AvailableBedDTO> result = new ArrayList<>();

        // 查询所有可用宿舍（status = 1 表示可用）
        LambdaQueryWrapper<Dormitory> dormitoryWrapper = new LambdaQueryWrapper<>();
        dormitoryWrapper.eq(Dormitory::getStatus, 1)
                .like(org.apache.commons.lang3.StringUtils.isNotBlank(keyword),Dormitory::getRoomNo,keyword)
                       .eq(Dormitory::getDeleted, 0).last("limit 10");
        List<Dormitory> dormitories = dormitoryMapper.selectList(dormitoryWrapper);

        for (Dormitory dormitory : dormitories) {
            AvailableBedDTO availableBedDTO = BeanUtil.copyProperties(dormitory, AvailableBedDTO.class);
            availableBedDTO.setDormitoryId(dormitory.getId());
            List<Bed> beds = bedMapper.selectList(Wrappers.<Bed>lambdaQuery()
                    .eq(Bed::getStatus, 1).eq(Bed::getDormitoryId,dormitory.getId()));
            List<BedInfoDTO> collect = beds.stream().map(v -> {
                BedInfoDTO bedInfoDTO = BeanUtil.copyProperties(v, BedInfoDTO.class);
                bedInfoDTO.setBedId(v.getId());
                return bedInfoDTO;
            }).collect(Collectors.toList());
            availableBedDTO.setBedList(collect);
            result.add(availableBedDTO);
        }
        return result;
    }
}