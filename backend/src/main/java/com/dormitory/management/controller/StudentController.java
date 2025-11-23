package com.dormitory.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.DormitoryAssignmentDTO;
import com.dormitory.management.dto.StudentDTO;
import com.dormitory.management.dto.StudentPageDTO;
import com.dormitory.management.dto.StudentUpdateDTO;
import com.dormitory.management.dto.StudentStatusUpdateDTO;
import com.dormitory.management.dto.ChangeDormitoryDTO;
import com.dormitory.management.dto.StudentCheckoutDTO;
import com.dormitory.management.service.StudentService;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.vo.DormitoryVO;
import com.dormitory.management.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生管理控制器
 */
@Tag(name = "学生管理", description = "学生相关接口")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final DormitoryService dormitoryService;

    /**
     * 获取当前用户名
     */
    private String getCurrentUser() {
        String username = UserContext.getCurrentUsername();
        return username != null ? username : "system";
    }

    @Operation(summary = "分页查询学生列表")
    @PostMapping("/page")
    public Result<Page<StudentVO>> getStudentPage(@RequestBody StudentPageDTO pageDTO) {
        Page<StudentVO> result = studentService.getStudentPage( pageDTO);
        return Result.success(result);
    }

    @Operation(summary = "根据ID获取学生详情")
    @GetMapping("/{id}")
    public Result<StudentVO> getStudentById(
            @Parameter(description = "学生ID") @PathVariable Long id) {
        StudentVO student = studentService.getStudentById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        return Result.success(student);
    }

    @Operation(summary = "新增学生")
    @PostMapping
    public Result<Void> createStudent(@Validated @RequestBody StudentDTO studentDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = studentService.createStudent(studentDTO, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新学生信息")
    @PostMapping("/update")
    public Result<Void> updateStudent(@Validated @RequestBody StudentUpdateDTO studentDTO) {
        try {
            StudentDTO studentDTOForService = new StudentDTO();
            studentDTOForService.setId(studentDTO.getId());
            studentDTOForService.setStudentNo(studentDTO.getStudentNo());
            studentDTOForService.setName(studentDTO.getName());
            studentDTOForService.setGender(studentDTO.getGender().equals("男") ? 1 : 0);
            studentDTOForService.setCollege(studentDTO.getCollege());
            studentDTOForService.setMajor(studentDTO.getMajor());
            studentDTOForService.setGrade(studentDTO.getGrade());
            studentDTOForService.setClassName(studentDTO.getClassName());
            studentDTOForService.setPhone(studentDTO.getPhone());
            studentDTOForService.setEmail(studentDTO.getEmail());
            studentDTOForService.setIdCard(studentDTO.getIdCard());
            studentDTOForService.setHomeAddress(studentDTO.getAddress());
            studentDTOForService.setEmergencyContact(studentDTO.getEmergencyContact());
            studentDTOForService.setEmergencyPhone(studentDTO.getEmergencyPhone());
            studentDTOForService.setDormitoryId(studentDTO.getDormitoryId() != null ? studentDTO.getDormitoryId().toString() : null);
            studentDTOForService.setBedNo(studentDTO.getBedNo());
            studentDTOForService.setStatus(studentDTO.getCheckInStatus());
            String currentUser = getCurrentUser();
            boolean success = studentService.updateStudent(studentDTOForService, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(
            @Parameter(description = "学生ID") @PathVariable Long id) {
        try {
            boolean success = studentService.deleteStudent(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "修改学生状态")
    @PostMapping("/status")
    public Result<Void> updateStudentStatus(@RequestBody StudentStatusUpdateDTO statusDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = studentService.updateStudentStatus(statusDTO.getStudentId(), statusDTO.getStatus(), currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "分配宿舍")
    @PostMapping("/{id}/assign-dormitory")
    public Result<Void> assignDormitory(
            @Parameter(description = "学生ID") @PathVariable String id,
            @Validated @RequestBody DormitoryAssignmentDTO assignmentDTO) {
        try {
            assignmentDTO.setStudentId(id);
            String currentUser = getCurrentUser();
            boolean success = studentService.assignDormitory(assignmentDTO, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("宿舍分配失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "调换宿舍")
    @PostMapping("/change-dormitory")
    public Result<Void> changeDormitory(@RequestBody ChangeDormitoryDTO changeDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = studentService.changeDormitory(
                    Long.parseLong(changeDTO.getStudentId()),
                    Long.parseLong(changeDTO.getNewDormitoryId()),
                    changeDTO.getNewBedNo(),
                    changeDTO.getReason(),
                    currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("宿舍调换失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "退宿处理")
    @PostMapping("/checkout")
    public Result<Void> checkoutDormitory(@RequestBody StudentCheckoutDTO checkoutDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = studentService.checkoutDormitory(
                    Long.parseLong(checkoutDTO.getStudentId()),
                    checkoutDTO.getReason(),
                    currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("退宿处理失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "根据宿舍ID获取学生列表")
    @GetMapping("/dormitory/{dormitoryId}")
    public Result<List<StudentVO>> getStudentsByDormitory(
            @Parameter(description = "宿舍ID") @PathVariable String dormitoryId) {
        List<StudentVO> students = studentService.getStudentsByDormitory(Long.parseLong(dormitoryId));
        return Result.success(students);
    }

    @Operation(summary = "获取学生统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStudentStatistics() {
        Map<String, Object> statistics = studentService.getStudentStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "批量导入学生")
    @PostMapping("/batch-import")
    public Result<Map<String, Object>> batchImportStudents(
            @RequestBody List<StudentDTO> students) {
        try {
            String currentUser = getCurrentUser();
            Map<String, Object> result = studentService.batchImportStudents(students, currentUser);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取可用宿舍列表")
    @GetMapping("/available-dormitories")
    public Result<List<Map<String, Object>>> getAvailableDormitories() {
        try {
            List<DormitoryVO> availableDormitories = dormitoryService.getAvailableDormitories(null);

            List<Map<String, Object>> result = availableDormitories.stream().map(dorm -> {
                Map<String, Object> dormMap = new java.util.HashMap<>();
                dormMap.put("id", dorm.getId());
                dormMap.put("buildingName", dorm.getBuildingName());
                dormMap.put("roomNo", dorm.getRoomNo());
                dormMap.put("availableBeds", dorm.getAvailableBeds());

                return dormMap;
            }).collect(java.util.stream.Collectors.toList());

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取可用宿舍列表失败: " + e.getMessage());
        }
    }
}