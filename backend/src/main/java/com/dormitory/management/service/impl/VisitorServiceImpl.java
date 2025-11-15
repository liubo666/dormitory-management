package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.DormitorySearchDTO;
import com.dormitory.management.dto.StudentSearchDTO;
import com.dormitory.management.dto.VisitorDTO;
import com.dormitory.management.dto.VisitorPageDTO;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.Student;
import com.dormitory.management.entity.Visitor;
import com.dormitory.management.enums.VisitorStatusEnum;
import com.dormitory.management.mapper.VisitorMapper;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.service.StudentService;
import com.dormitory.management.service.VisitorService;
import org.springframework.util.StringUtils;
import com.dormitory.management.vo.DormitorySearchVO;
import com.dormitory.management.vo.StudentSearchVO;
import com.dormitory.management.vo.VisitorVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 访客登记服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    private final DormitoryService dormitoryService;
    private final StudentService studentService;

    @Override
    public IPage<VisitorVO> getVisitorPage(VisitorPageDTO pageDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();

        // 访客姓名条件
        if (StringUtils.hasText(pageDTO.getVisitorName())) {
            queryWrapper.like(Visitor::getVisitorName, pageDTO.getVisitorName());
        }

        // 访客手机号条件
        if (StringUtils.hasText(pageDTO.getVisitorPhone())) {
            queryWrapper.like(Visitor::getVisitorPhone, pageDTO.getVisitorPhone());
        }

        // 被访学生姓名条件
        if (StringUtils.hasText(pageDTO.getVisitStudentName())) {
            queryWrapper.like(Visitor::getVisitStudentName, pageDTO.getVisitStudentName());
        }

        // 被访学生学号条件
        if (StringUtils.hasText(pageDTO.getVisitStudentNo())) {
            queryWrapper.like(Visitor::getVisitStudentNo, pageDTO.getVisitStudentNo());
        }

        // 宿舍ID条件
        if (pageDTO.getRoomId() != null) {
            queryWrapper.eq(Visitor::getRoomId, pageDTO.getRoomId());
        }

        // 宿舍号条件
        if (StringUtils.hasText(pageDTO.getRoomNo())) {
            queryWrapper.like(Visitor::getRoomNo, pageDTO.getRoomNo());
        }

        // 状态条件
        if (pageDTO.getStatus() != null) {
            queryWrapper.eq(Visitor::getStatus, pageDTO.getStatus());
        }

        // 日期范围条件
        if (StringUtils.hasText(pageDTO.getStartDate())) {
            queryWrapper.ge(Visitor::getExpectedVisitTime,
                LocalDateTime.parse(pageDTO.getStartDate() + " 00:00:00"));
        }
        if (StringUtils.hasText(pageDTO.getEndDate())) {
            queryWrapper.le(Visitor::getExpectedVisitTime,
                LocalDateTime.parse(pageDTO.getEndDate() + " 23:59:59"));
        }

        // 按预计访问时间降序排序
        queryWrapper.orderByDesc(Visitor::getExpectedVisitTime)
                   .orderByDesc(Visitor::getCreateTime);

        // 执行分页查询
        Page<Visitor> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        Page<Visitor> result = this.page(page, queryWrapper);

        // 转换为VO对象
        List<VisitorVO> voList = result.getRecords().stream().map(visitor -> {
            VisitorVO vo = new VisitorVO();
            BeanUtils.copyProperties(visitor, vo);

            // 获取宿舍信息
            if (visitor.getRoomId() != null) {
                Dormitory dormitory = dormitoryService.getById(visitor.getRoomId());
                if (dormitory != null) {
                    vo.setRoomNo(dormitory.getRoomNo());
                    vo.setBuildingName(dormitory.getBuildingName());
                }
            }

            // 设置状态文本
            setVisitorDisplayText(vo);

            return vo;
        }).collect(Collectors.toList());

        // 构建返回结果
        Page<VisitorVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public VisitorVO getVisitorById(Long id) {
        Visitor visitor = getById(id);
        if (visitor == null) {
            return null;
        }

        VisitorVO vo = new VisitorVO();
        BeanUtils.copyProperties(visitor, vo);

        // 获取宿舍信息
        if (visitor.getRoomId() != null) {
            Dormitory dormitory = dormitoryService.getById(visitor.getRoomId());
            if (dormitory != null) {
                vo.setRoomNo(dormitory.getRoomNo());
                vo.setBuildingName(dormitory.getBuildingName());
            }
        }

        // 设置状态文本
        setVisitorDisplayText(vo);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addVisitor(VisitorDTO visitorDTO, String createBy) {
        try {
            // 验证宿舍是否存在
            Dormitory dormitory = dormitoryService.getById(visitorDTO.getRoomId());
            if (dormitory == null) {
                throw new RuntimeException("宿舍不存在");
            }

            Visitor visitor = new Visitor();
            BeanUtils.copyProperties(visitorDTO, visitor);

            // 设置宿舍信息
            visitor.setRoomNo(dormitory.getRoomNo());
            visitor.setBuildingName(dormitory.getBuildingName());

            // 解析时间字段
            if (StringUtils.hasText(visitorDTO.getExpectedVisitTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime expectedVisitTime = LocalDateTime.parse(visitorDTO.getExpectedVisitTime(), formatter);
                visitor.setExpectedVisitTime(expectedVisitTime);
            }

            if (StringUtils.hasText(visitorDTO.getActualArrivalTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime actualArrivalTime = LocalDateTime.parse(visitorDTO.getActualArrivalTime(), formatter);
                visitor.setActualArrivalTime(actualArrivalTime);
            }

            if (StringUtils.hasText(visitorDTO.getLeaveTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime leaveTime = LocalDateTime.parse(visitorDTO.getLeaveTime(), formatter);
                visitor.setLeaveTime(leaveTime);
            }

            // 设置登记人信息
            visitor.setRegistrarId(UserContext.getCurrentUserId());
            visitor.setRegistrarName(UserContext.getCurrentRealName());

            // 设置默认状态
            if (visitor.getStatus() == null) {
                visitor.setStatus(VisitorStatusEnum.PENDING.getCode());
            }

            boolean result = save(visitor);
            if (result) {
                log.info("新增访客登记记录成功，访客姓名: {}", visitorDTO.getVisitorName());
            }
            return result;
        } catch (Exception e) {
            log.error("新增访客登记记录失败", e);
            throw new RuntimeException("新增访客登记记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVisitor(VisitorDTO visitorDTO, String updateBy) {
        try {
            Visitor visitor = getById(visitorDTO.getId());
            if (visitor == null) {
                throw new RuntimeException("访客记录不存在");
            }

            // 验证宿舍是否存在
            Dormitory dormitory = dormitoryService.getById(visitorDTO.getRoomId());
            if (dormitory == null) {
                throw new RuntimeException("宿舍不存在");
            }

            BeanUtils.copyProperties(visitorDTO, visitor);

            // 设置宿舍信息
            visitor.setRoomNo(dormitory.getRoomNo());
            visitor.setBuildingName(dormitory.getBuildingName());

            // 解析时间字段
            if (StringUtils.hasText(visitorDTO.getExpectedVisitTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime expectedVisitTime = LocalDateTime.parse(visitorDTO.getExpectedVisitTime(), formatter);
                visitor.setExpectedVisitTime(expectedVisitTime);
            }

            if (StringUtils.hasText(visitorDTO.getActualArrivalTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime actualArrivalTime = LocalDateTime.parse(visitorDTO.getActualArrivalTime(), formatter);
                visitor.setActualArrivalTime(actualArrivalTime);
            }

            if (StringUtils.hasText(visitorDTO.getLeaveTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime leaveTime = LocalDateTime.parse(visitorDTO.getLeaveTime(), formatter);
                visitor.setLeaveTime(leaveTime);
            }

            boolean result = updateById(visitor);
            if (result) {
                log.info("更新访客登记记录成功，ID: {}", visitorDTO.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新访客登记记录失败", e);
            throw new RuntimeException("更新访客登记记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteVisitor(Long id) {
        try {
            Visitor visitor = getById(id);
            if (visitor == null) {
                throw new RuntimeException("访客记录不存在");
            }

            boolean result = removeById(id);
            if (result) {
                log.info("删除访客登记记录成功，ID: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除访客登记记录失败", e);
            throw new RuntimeException("删除访客登记记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchDeleteVisitor(List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;

        for (Long id : ids) {
            try {
                if (deleteVisitor(id)) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
                log.error("删除访客记录失败，ID: {}", id, e);
            }
        }

        result.put("totalCount", ids.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("success", failCount == 0);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkIn(Long id) {
        try {
            Visitor visitor = getById(id);
            if (visitor == null) {
                throw new RuntimeException("访客记录不存在");
            }

            if (!VisitorStatusEnum.PENDING.getCode().equals(visitor.getStatus())) {
                throw new RuntimeException("只有待访问状态的访客才能签到");
            }

            visitor.setActualArrivalTime(LocalDateTime.now());
            visitor.setStatus(VisitorStatusEnum.IN_PROGRESS.getCode());

            boolean result = updateById(visitor);
            if (result) {
                log.info("访客签到成功，ID: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("访客签到失败", e);
            throw new RuntimeException("访客签到失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkOut(Long id) {
        try {
            Visitor visitor = getById(id);
            if (visitor == null) {
                throw new RuntimeException("访客记录不存在");
            }

            if (!VisitorStatusEnum.IN_PROGRESS.getCode().equals(visitor.getStatus())) {
                throw new RuntimeException("只有访问中状态的访客才能签退");
            }

            visitor.setLeaveTime(LocalDateTime.now());
            visitor.setStatus(VisitorStatusEnum.COMPLETED.getCode());

            boolean result = updateById(visitor);
            if (result) {
                log.info("访客签退成功，ID: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("访客签退失败", e);
            throw new RuntimeException("访客签退失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelVisit(Long id, String reason) {
        try {
            Visitor visitor = getById(id);
            if (visitor == null) {
                throw new RuntimeException("访客记录不存在");
            }

            if (VisitorStatusEnum.COMPLETED.getCode().equals(visitor.getStatus())) {
                throw new RuntimeException("已完成的访客不能取消");
            }

            visitor.setStatus(VisitorStatusEnum.CANCELLED.getCode());
            if (StringUtils.hasText(reason)) {
                visitor.setRemarks(visitor.getRemarks() + " [取消原因: " + reason + "]");
            }

            boolean result = updateById(visitor);
            if (result) {
                log.info("取消访客访问成功，ID: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("取消访客访问失败", e);
            throw new RuntimeException("取消访客访问失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getDormitoryOptions() {
        try {
            // 获取所有可用宿舍
            LambdaQueryWrapper<Dormitory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dormitory::getStatus, 1); // 只获取可用状态的宿舍
            queryWrapper.orderByAsc(Dormitory::getBuildingNo, Dormitory::getRoomNo);

            List<Dormitory> dormitories = dormitoryService.list(queryWrapper);

            return dormitories.stream().map(dormitory -> {
                Map<String, Object> option = new HashMap<>();
                option.put("id", dormitory.getId());
                option.put("roomNo", dormitory.getRoomNo());
                option.put("buildingName", dormitory.getBuildingName());
                option.put("buildingNo", dormitory.getBuildingNo());
                option.put("label", dormitory.getBuildingName() + " " + dormitory.getRoomNo());
                option.put("value", dormitory.getId());
                return option;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取宿舍选项失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getVisitorStatistics(String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();

            // 日期范围条件
            if (StringUtils.hasText(startDate)) {
                queryWrapper.ge(Visitor::getExpectedVisitTime,
                    LocalDateTime.parse(startDate + " 00:00:00"));
            }
            if (StringUtils.hasText(endDate)) {
                queryWrapper.le(Visitor::getExpectedVisitTime,
                    LocalDateTime.parse(endDate + " 23:59:59"));
            }

            List<Visitor> visitors = this.list(queryWrapper);

            // 统计各状态数量
            Map<Integer, Long> statusCount = visitors.stream()
                .collect(Collectors.groupingBy(Visitor::getStatus, Collectors.counting()));

            statistics.put("totalCount", visitors.size());
            statistics.put("pendingCount", statusCount.getOrDefault(VisitorStatusEnum.PENDING.getCode(), 0L));
            statistics.put("inProgressCount", statusCount.getOrDefault(VisitorStatusEnum.IN_PROGRESS.getCode(), 0L));
            statistics.put("completedCount", statusCount.getOrDefault(VisitorStatusEnum.COMPLETED.getCode(), 0L));
            statistics.put("cancelledCount", statusCount.getOrDefault(VisitorStatusEnum.CANCELLED.getCode(), 0L));

            // 今日访问统计
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            LambdaQueryWrapper<Visitor> todayQueryWrapper = new LambdaQueryWrapper<>();
            todayQueryWrapper.ge(Visitor::getExpectedVisitTime, todayStart);
            todayQueryWrapper.le(Visitor::getExpectedVisitTime, todayEnd);

            List<Visitor> todayVisitors = this.list(todayQueryWrapper);
            statistics.put("todayCount", todayVisitors.size());

        } catch (Exception e) {
            log.error("获取访客统计信息失败", e);
        }

        return statistics;
    }

    @Override
    public IPage<StudentSearchVO> searchStudents(StudentSearchDTO searchDTO) {
        try {
            // 构建学生查询条件
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();

            // 关键字模糊查询（支持学生姓名、学号）
            if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
                queryWrapper.and(wrapper ->
                    wrapper.like(Student::getName, searchDTO.getKeyword())
                           .or()
                           .like(Student::getStudentNo, searchDTO.getKeyword())
                );
            }

            // 只查询有效学生（在校状态）
            queryWrapper.eq(Student::getStatus, 1);

            // 按学号排序
            queryWrapper.orderByAsc(Student::getStudentNo);

            // 执行分页查询
            Page<Student> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
            Page<Student> result = studentService.page(page, queryWrapper);

            // 转换为VO对象
            List<StudentSearchVO> voList = result.getRecords().stream().map(student -> {
                StudentSearchVO vo = new StudentSearchVO();
                BeanUtils.copyProperties(student, vo);

                // 设置显示标签和选项值
                vo.setLabel(student.getStudentNo() + " - " + student.getName());
                vo.setValue(student.getId());

                return vo;
            }).collect(Collectors.toList());

            // 构建返回结果
            Page<StudentSearchVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
            voPage.setRecords(voList);

            return voPage;
        } catch (Exception e) {
            log.error("搜索学生信息失败", e);
            return new Page<>(searchDTO.getCurrent(), searchDTO.getSize(), 0);
        }
    }

    @Override
    public IPage<DormitorySearchVO> searchDormitories(DormitorySearchDTO searchDTO) {
        try {
            // 构建宿舍查询条件
            LambdaQueryWrapper<Dormitory> queryWrapper = new LambdaQueryWrapper<>();

            // 关键字模糊查询（支持宿舍号、宿舍楼名称）
            if (StringUtils.hasText(searchDTO.getKeyword())) {
                queryWrapper.and(wrapper ->
                    wrapper.like(Dormitory::getRoomNo, searchDTO.getKeyword())
                           .or()
                           .like(Dormitory::getBuildingName, searchDTO.getKeyword())
                );
            }

            // 宿舍楼编号筛选
            if (StringUtils.hasText(searchDTO.getBuildingNo())) {
                queryWrapper.eq(Dormitory::getBuildingNo, searchDTO.getBuildingNo());
            }

            // 状态筛选（默认只查询可用宿舍）
            if (searchDTO.getStatus() != null) {
                queryWrapper.eq(Dormitory::getStatus, searchDTO.getStatus());
            } else {
                queryWrapper.eq(Dormitory::getStatus, 1);
            }

            // 按宿舍楼编号、宿舍号排序
            queryWrapper.orderByAsc(Dormitory::getBuildingNo, Dormitory::getRoomNo);

            // 执行分页查询
            Page<Dormitory> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
            Page<Dormitory> result = dormitoryService.page(page, queryWrapper);

            // 转换为VO对象
            List<DormitorySearchVO> voList = result.getRecords().stream().map(dormitory -> {
                DormitorySearchVO vo = new DormitorySearchVO();
                BeanUtils.copyProperties(dormitory, vo);

                // 设置显示标签和选项值
                vo.setLabel(dormitory.getBuildingName() + " " + dormitory.getRoomNo());
                vo.setValue(dormitory.getId());

                return vo;
            }).collect(Collectors.toList());

            // 构建返回结果
            Page<DormitorySearchVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
            voPage.setRecords(voList);

            return voPage;
        } catch (Exception e) {
            log.error("搜索宿舍信息失败", e);
            return new Page<>(searchDTO.getCurrent(), searchDTO.getSize(), 0);
        }
    }

    /**
     * 设置访客显示文本
     */
    private void setVisitorDisplayText(VisitorVO visitor) {
        if (visitor.getStatus() != null) {
            visitor.setStatusText(VisitorStatusEnum.getDescriptionByCode(visitor.getStatus()));
        }
    }
}