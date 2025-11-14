package com.dormitory.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.DormitoryAssignmentDTO;
import com.dormitory.management.dto.StudentDTO;
import com.dormitory.management.dto.StudentPageDTO;
import com.dormitory.management.entity.Student;
import com.dormitory.management.vo.StudentVO;

import java.util.List;
import java.util.Map;

/**
 * 学生Service接口
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生列表
     *
     * @param page 分页对象
     * @param pageDTO 查询条件
     * @return 分页结果
     */
    Page<StudentVO> getStudentPage(Page<StudentVO> page, StudentPageDTO pageDTO);

    /**
     * 根据ID获取学生详情
     *
     * @param id 学生ID
     * @return 学生详情
     */
    StudentVO getStudentById(String id);

    /**
     * 根据学号获取学生信息
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    Student getStudentByStudentNo(String studentNo);

    /**
     * 新增学生
     *
     * @param studentDTO 学生信息
     * @param createBy 创建人
     * @return 是否成功
     */
    boolean createStudent(StudentDTO studentDTO, String createBy);

    /**
     * 更新学生信息
     *
     * @param studentDTO 学生信息
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean updateStudent(StudentDTO studentDTO, String updateBy);

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 是否成功
     */
    boolean deleteStudent(String id);

    /**
     * 修改学生状态
     *
     * @param id 学生ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean updateStudentStatus(String id, Integer status, String updateBy);

    /**
     * 分配宿舍
     *
     * @param assignmentDTO 宿舍分配信息
     * @param updateBy 操作人
     * @return 是否成功
     */
    boolean assignDormitory(DormitoryAssignmentDTO assignmentDTO, String updateBy);

    /**
     * 调换宿舍
     *
     * @param studentId 学生ID
     * @param newDormitoryId 新宿舍ID
     * @param newBedNo 新床位号
     * @param reason 调换原因
     * @param updateBy 操作人
     * @return 是否成功
     */
    boolean changeDormitory(String studentId, String newDormitoryId, String newBedNo, String reason, String updateBy);

    /**
     * 退宿处理
     *
     * @param studentId 学生ID
     * @param reason 退宿原因
     * @param updateBy 操作人
     * @return 是否成功
     */
    boolean checkoutDormitory(String studentId, String reason, String updateBy);

    /**
     * 根据宿舍ID获取学生列表
     *
     * @param dormitoryId 宿舍ID
     * @return 学生列表
     */
    List<StudentVO> getStudentsByDormitory(String dormitoryId);

    /**
     * 获取学生统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getStudentStatistics();

    /**
     * 批量导入学生
     *
     * @param students 学生列表
     * @param createBy 创建人
     * @return 导入结果
     */
    Map<String, Object> batchImportStudents(List<StudentDTO> students, String createBy);
}