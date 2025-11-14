package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.dto.DormitoryAssignmentDTO;
import com.dormitory.management.dto.StudentDTO;
import com.dormitory.management.dto.StudentPageDTO;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.Student;
import com.dormitory.management.mapper.DormitoryMapper;
import com.dormitory.management.mapper.StudentMapper;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.service.StudentService;
import com.dormitory.management.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生Service实现类
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final DormitoryMapper dormitoryMapper;

    @Override
    public Page<StudentVO> getStudentPage(Page<StudentVO> page, StudentPageDTO pageDTO) {
        // 使用MyBatis-Plus的LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getDeleted, 0);

        if (StringUtils.hasText(pageDTO.getStudentNo())) {
            queryWrapper.like(Student::getStudentNo, pageDTO.getStudentNo());
        }

        if (StringUtils.hasText(pageDTO.getName())) {
            queryWrapper.like(Student::getName, pageDTO.getName());
        }

        if (pageDTO.getGender() != null) {
            queryWrapper.eq(Student::getGender, pageDTO.getGender());
        }

        if (StringUtils.hasText(pageDTO.getCollege())) {
            queryWrapper.like(Student::getCollege, pageDTO.getCollege());
        }

        if (StringUtils.hasText(pageDTO.getMajor())) {
            queryWrapper.like(Student::getMajor, pageDTO.getMajor());
        }

        if (StringUtils.hasText(pageDTO.getClassName())) {
            queryWrapper.like(Student::getClassName, pageDTO.getClassName());
        }

        if (StringUtils.hasText(pageDTO.getGrade())) {
            queryWrapper.eq(Student::getGrade, pageDTO.getGrade());
        }

        if (StringUtils.hasText(pageDTO.getDormitoryId())) {
            queryWrapper.eq(Student::getDormitoryId, pageDTO.getDormitoryId());
        }

        if (pageDTO.getStatus() != null) {
            queryWrapper.eq(Student::getStatus, pageDTO.getStatus());
        }

        // 按学号排序
        queryWrapper.orderByAsc(Student::getStudentNo);

        // 查询学生列表
        Page<Student> studentPage = this.page(new Page<>(page.getCurrent(), page.getSize()), queryWrapper);

        // 转换为VO对象
        List<StudentVO> studentVOList = studentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<StudentVO> result = new Page<>(page.getCurrent(), page.getSize(), studentPage.getTotal());
        result.setRecords(studentVOList);

        return result;
    }

    @Override
    public StudentVO getStudentById(String id) {
        Student student = this.getById(id);
        if (student == null) {
            return null;
        }

        return convertToVO(student);
    }

    @Override
    public Student getStudentByStudentNo(String studentNo) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentNo, studentNo)
                   .eq(Student::getDeleted, 0);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createStudent(StudentDTO studentDTO, String createBy) {
        // 检查学号是否已存在
        Student existingStudent = getStudentByStudentNo(studentDTO.getStudentNo());
        if (existingStudent != null) {
            throw new RuntimeException("学号已存在");
        }

        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);

        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        student.setCreateBy(createBy);
        student.setUpdateBy(createBy);
        student.setDeleted(0);

        return this.save(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(StudentDTO studentDTO, String updateBy) {
        Student existing = this.getById(studentDTO.getId());
        if (existing == null) {
            throw new RuntimeException("学生不存在");
        }

        // 如果学号发生变化，检查新学号是否已存在
        if (!existing.getStudentNo().equals(studentDTO.getStudentNo())) {
            Student duplicate = getStudentByStudentNo(studentDTO.getStudentNo());
            if (duplicate != null && !duplicate.getId().equals(studentDTO.getId())) {
                throw new RuntimeException("学号已存在");
            }
        }

        BeanUtils.copyProperties(studentDTO, existing);
        existing.setUpdateTime(LocalDateTime.now());
        existing.setUpdateBy(updateBy);

        return this.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(String id) {
        Student student = this.getById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查是否已分配宿舍
        if (StringUtils.hasText(student.getDormitoryId())) {
            throw new RuntimeException("学生已分配宿舍，请先退宿后再删除");
        }

        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudentStatus(String id, Integer status, String updateBy) {
        Student student = this.getById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 如果设置为毕业状态，检查是否已退宿
        if (status == 2 && StringUtils.hasText(student.getDormitoryId())) {
            throw new RuntimeException("学生已分配宿舍，请先退宿后再设置为毕业状态");
        }

        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getId, id)
                    .set(Student::getStatus, status)
                    .set(Student::getUpdateTime, LocalDateTime.now())
                    .set(Student::getUpdateBy, updateBy);

        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignDormitory(DormitoryAssignmentDTO assignmentDTO, String updateBy) {
        // 检查学生是否存在
        Student student = this.getById(assignmentDTO.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查学生是否为在校状态
        if (student.getStatus() != 1) {
            throw new RuntimeException("只有在校生才能分配宿舍");
        }

        // 检查宿舍是否存在
        Dormitory dormitory = dormitoryMapper.selectById(assignmentDTO.getDormitoryId());
        if (dormitory == null || dormitory.getDeleted() == 1) {
            throw new RuntimeException("宿舍不存在");
        }

        // 检查宿舍是否可用
        if (dormitory.getStatus() != 1) {
            throw new RuntimeException("宿舍状态不可用");
        }

//        // 检查是否有空床位
//        if (dormitory.getOccupiedBeds() >= dormitory.getBedCount()) {
//            throw new RuntimeException("宿舍已满，无法分配");
//        }

        // 检查学生是否已分配宿舍
        if (StringUtils.hasText(student.getDormitoryId())) {
            throw new RuntimeException("学生已分配宿舍，请先退宿后再重新分配");
        }

        // 检查床位是否已被占用
        LambdaQueryWrapper<Student> bedQuery = new LambdaQueryWrapper<>();
        bedQuery.eq(Student::getDormitoryId, assignmentDTO.getDormitoryId())
               .eq(Student::getBedNo, assignmentDTO.getBedNo())
               .eq(Student::getDeleted, 0);
        Student bedStudent = this.getOne(bedQuery);
        if (bedStudent != null) {
            throw new RuntimeException("该床位已被其他学生占用");
        }

        // 分配宿舍
        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getId, assignmentDTO.getStudentId())
                    .set(Student::getDormitoryId, assignmentDTO.getDormitoryId())
                    .set(Student::getBedNo, assignmentDTO.getBedNo())
                    .set(Student::getUpdateTime, LocalDateTime.now())
                    .set(Student::getUpdateBy, updateBy);

        boolean result = this.update(updateWrapper);

        if (result) {
            // 更新宿舍入住人数
            LambdaUpdateWrapper<Dormitory> dormitoryUpdate = new LambdaUpdateWrapper<>();
            dormitoryUpdate.eq(Dormitory::getId, assignmentDTO.getDormitoryId())
                          .set(Dormitory::getOccupiedBeds, dormitory.getOccupiedBeds() + 1)
                          .set(Dormitory::getUpdateTime, LocalDateTime.now());
            dormitoryMapper.update(null, dormitoryUpdate);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeDormitory(String studentId, String newDormitoryId, String newBedNo, String reason, String updateBy) {
        // 检查学生是否存在
        Student student = this.getById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查是否已分配宿舍
        if (!StringUtils.hasText(student.getDormitoryId())) {
            throw new RuntimeException("学生未分配宿舍，无法调换");
        }

        // 检查新宿舍是否可用
        Dormitory newDormitory = dormitoryMapper.selectById(newDormitoryId);
        if (newDormitory == null || newDormitory.getDeleted() == 1) {
            throw new RuntimeException("新宿舍不存在");
        }

        if (newDormitory.getStatus() != 1) {
            throw new RuntimeException("新宿舍状态不可用");
        }

        // 检查新床位是否可用
        if (!student.getDormitoryId().equals(newDormitoryId)) {
            // 换到不同宿舍，检查床位数量
//            if (newDormitory.getOccupiedBeds() >= newDormitory.getBedCount()) {
//                throw new RuntimeException("新宿舍已满，无法调换");
//            }
        }

        // 检查新床位是否已被占用
        LambdaQueryWrapper<Student> bedQuery = new LambdaQueryWrapper<>();
        bedQuery.eq(Student::getDormitoryId, newDormitoryId)
               .eq(Student::getBedNo, newBedNo)
               .eq(Student::getDeleted, 0)
               .ne(Student::getId, studentId);
        Student bedStudent = this.getOne(bedQuery);
        if (bedStudent != null) {
            throw new RuntimeException("新床位已被其他学生占用");
        }

        String oldDormitoryId = student.getDormitoryId();

        // 调换宿舍
        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getId, studentId)
                    .set(Student::getDormitoryId, newDormitoryId)
                    .set(Student::getBedNo, newBedNo)
                    .set(Student::getUpdateTime, LocalDateTime.now())
                    .set(Student::getUpdateBy, updateBy);

        boolean result = this.update(updateWrapper);

        if (result) {
            // 更新旧宿舍入住人数
            if (!oldDormitoryId.equals(newDormitoryId)) {
                Dormitory oldDormitory = dormitoryMapper.selectById(oldDormitoryId);
                LambdaUpdateWrapper<Dormitory> oldUpdate = new LambdaUpdateWrapper<>();
                oldUpdate.eq(Dormitory::getId, oldDormitoryId)
                        .set(Dormitory::getOccupiedBeds, Math.max(0, oldDormitory.getOccupiedBeds() - 1))
                        .set(Dormitory::getUpdateTime, LocalDateTime.now());
                dormitoryMapper.update(null, oldUpdate);

                // 更新新宿舍入住人数
                LambdaUpdateWrapper<Dormitory> newUpdate = new LambdaUpdateWrapper<>();
                newUpdate.eq(Dormitory::getId, newDormitoryId)
                        .set(Dormitory::getOccupiedBeds, newDormitory.getOccupiedBeds() + 1)
                        .set(Dormitory::getUpdateTime, LocalDateTime.now());
                dormitoryMapper.update(null, newUpdate);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkoutDormitory(String studentId, String reason, String updateBy) {
        // 检查学生是否存在
        Student student = this.getById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查是否已分配宿舍
        if (!StringUtils.hasText(student.getDormitoryId())) {
            throw new RuntimeException("学生未分配宿舍，无法退宿");
        }

        String dormitoryId = student.getDormitoryId();

        // 退宿
        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getId, studentId)
                    .set(Student::getDormitoryId, null)
                    .set(Student::getBedNo, null)
                    .set(Student::getUpdateTime, LocalDateTime.now())
                    .set(Student::getUpdateBy, updateBy);

        boolean result = this.update(updateWrapper);

        if (result) {
            // 更新宿舍入住人数
            Dormitory dormitory = dormitoryMapper.selectById(dormitoryId);
            LambdaUpdateWrapper<Dormitory> dormitoryUpdate = new LambdaUpdateWrapper<>();
            dormitoryUpdate.eq(Dormitory::getId, dormitoryId)
                          .set(Dormitory::getOccupiedBeds, Math.max(0, dormitory.getOccupiedBeds() - 1))
                          .set(Dormitory::getUpdateTime, LocalDateTime.now());
            dormitoryMapper.update(null, dormitoryUpdate);
        }

        return result;
    }

    @Override
    public List<StudentVO> getStudentsByDormitory(String dormitoryId) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getDormitoryId, dormitoryId)
                   .eq(Student::getDeleted, 0)
                   .orderByAsc(Student::getBedNo);

        List<Student> students = this.list(queryWrapper);
        return students.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStudentStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总学生数
        LambdaQueryWrapper<Student> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(Student::getDeleted, 0);
        long totalCount = this.count(totalQuery);
        statistics.put("totalCount", totalCount);

        // 按状态统计
        LambdaQueryWrapper<Student> statusQuery = new LambdaQueryWrapper<>();
        statusQuery.eq(Student::getDeleted, 0)
                  .select(Student::getStatus);
        List<Map<String, Object>> statusStats = this.listMaps(statusQuery);

        Map<Integer, Long> statusCountMap = new HashMap<>();
        statusCountMap.put(0, 0L); // 休学
        statusCountMap.put(1, 0L); // 在校
        statusCountMap.put(2, 0L); // 毕业
        statusCountMap.put(3, 0L); // 退学

        for (Map<String, Object> stat : statusStats) {
            Integer status = (Integer) stat.get("status");
            statusCountMap.put(status, statusCountMap.getOrDefault(status, 0L) + 1);
        }

        statistics.put("statusStats", statusCountMap);

        // 入住学生数
        LambdaQueryWrapper<Student> dormitoryQuery = new LambdaQueryWrapper<>();
        dormitoryQuery.eq(Student::getDeleted, 0)
                      .isNotNull(Student::getDormitoryId);
        long dormitoryCount = this.count(dormitoryQuery);
        statistics.put("dormitoryCount", dormitoryCount);

        // 按性别统计
        LambdaQueryWrapper<Student> genderQuery = new LambdaQueryWrapper<>();
        genderQuery.eq(Student::getDeleted, 0)
                   .select(Student::getGender);
        List<Map<String, Object>> genderStats = this.listMaps(genderQuery);

        Map<Integer, Long> genderCountMap = new HashMap<>();
        genderCountMap.put(0, 0L); // 女
        genderCountMap.put(1, 0L); // 男

        for (Map<String, Object> stat : genderStats) {
            Integer gender = (Integer) stat.get("gender");
            genderCountMap.put(gender, genderCountMap.getOrDefault(gender, 0L) + 1);
        }

        statistics.put("genderStats", genderCountMap);

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchImportStudents(List<StudentDTO> students, String createBy) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new java.util.ArrayList<>();

        for (StudentDTO studentDTO : students) {
            try {
                // 检查必填字段
                if (!StringUtils.hasText(studentDTO.getStudentNo()) || !StringUtils.hasText(studentDTO.getName())) {
                    failCount++;
                    errorMessages.add("学号或姓名为空，导入失败: " + studentDTO.getStudentNo());
                    continue;
                }

                // 检查学号是否重复
                Student existing = getStudentByStudentNo(studentDTO.getStudentNo());
                if (existing != null) {
                    failCount++;
                    errorMessages.add("学号重复，导入失败: " + studentDTO.getStudentNo());
                    continue;
                }

                // 设置默认值
                if (studentDTO.getStatus() == null) {
                    studentDTO.setStatus(1); // 默认在校状态
                }

                // 创建学生
                boolean success = createStudent(studentDTO, createBy);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("创建失败: " + studentDTO.getStudentNo());
                }
            } catch (Exception e) {
                failCount++;
                errorMessages.add("导入失败: " + studentDTO.getStudentNo() + " - " + e.getMessage());
            }
        }

        result.put("totalCount", students.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);

        return result;
    }

    /**
     * 将Student实体转换为StudentVO
     *
     * @param student 学生实体
     * @return 学生VO对象
     */
    private StudentVO convertToVO(Student student) {
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(student, studentVO);

        // 设置性别文本
        studentVO.setGenderText(student.getGender() == 1 ? "男" : "女");

        // 计算年龄
        if (student.getBirthDate() != null) {
            int age = Period.between(student.getBirthDate(), LocalDate.now()).getYears();
            studentVO.setAge(age);
        }

        // 设置状态文本
        studentVO.setStatusText(getStatusText(student.getStatus()));

        // 查询宿舍信息
        if (StringUtils.hasText(student.getDormitoryId())) {
//            Dormitory dormitory = dormitoryMapper.selectById(student.getDormitoryId());
//            if (dormitory != null && dormitory.getDeleted() == 0) {
//                studentVO.setRoomNo(dormitory.getRoomNo());
//
//                // 查询楼栋信息
//                LambdaQueryWrapper<Dormitory> buildingQuery = new LambdaQueryWrapper<>();
//                buildingQuery
//                           .eq(Dormitory::getDeleted, 0);
//                Dormitory buildingDormitory = dormitoryMapper.selectOne(buildingQuery);
//                if (buildingDormitory != null) {
//                    // 这里需要通过关联查询获取楼栋名称，简化处理
////                    studentVO.setBuildingName(dormitory.getBuildingId());
//                }
//            }
        }

        return studentVO;
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
                return "休学";
            case 1:
                return "在校";
            case 2:
                return "毕业";
            case 3:
                return "退学";
            default:
                return "未知";
        }
    }
}