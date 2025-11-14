package com.dormitory.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.AvailableBedDTO;
import com.dormitory.management.dto.AvailableStudentDTO;
import com.dormitory.management.dto.CheckInDTO;
import com.dormitory.management.dto.CheckInPageDTO;
import com.dormitory.management.service.CheckInService;
import com.dormitory.management.vo.CheckInVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 入住分配管理控制器
 */
@Tag(name = "入住分配管理", description = "入住分配相关接口")
@RestController
@RequestMapping("/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    /**
     * 获取当前用户名
     */
    private String getCurrentUser() {
        String username = UserContext.getCurrentUsername();
        return username != null ? username : "system";
    }

    @Operation(summary = "分页查询入住分配列表")
    @PostMapping("/page")
    public Result<Page<CheckInVO>> getCheckInPage(@RequestBody CheckInPageDTO pageDTO) {
        Page<CheckInVO> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        Page<CheckInVO> result = checkInService.getCheckInPage(page, pageDTO);
        return Result.success(result);
    }

    @Operation(summary = "根据ID获取入住分配详情")
    @GetMapping("/{id}")
    public Result<CheckInVO> getCheckInById(
            @Parameter(description = "入住记录ID") @PathVariable String id) {
        CheckInVO checkIn = checkInService.getCheckInById(id);
        if (checkIn == null) {
            return Result.error("入住记录不存在");
        }
        return Result.success(checkIn);
    }

    @Operation(summary = "提交入住申请")
    @PostMapping("/application")
    public Result<Void> submitApplication(@Validated @RequestBody CheckInDTO checkInDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.submitCheckInApplication(checkInDTO, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("提交申请失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "审批入住申请")
    @PutMapping("/{id}/approve")
    public Result<Void> approveApplication(
            @Parameter(description = "入住记录ID") @PathVariable String id,
            @Parameter(description = "审批状态") @RequestParam Integer status,
            @Parameter(description = "审批备注") @RequestParam(required = false) String approvalRemark) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.approveCheckIn(id, status, approvalRemark, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("审批失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "分配宿舍")
    @PutMapping("/{id}/assign-dormitory")
    public Result<Void> assignDormitory(
            @Parameter(description = "入住记录ID") @PathVariable String id,
            @Parameter(description = "宿舍ID") @RequestParam String dormitoryId,
            @Parameter(description = "床位ID") @RequestParam String bedId,
            @Parameter(description = "床位号") @RequestParam String bedNo) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.assignDormitory(id, dormitoryId, bedId, bedNo, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("分配宿舍失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "退宿处理")
    @PutMapping("/{id}/checkout")
    public Result<Void> checkout(
            @Parameter(description = "入住记录ID") @PathVariable String id,
            @Parameter(description = "退宿原因") @RequestParam(required = false) String checkoutReason) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.checkout(id, checkoutReason, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("退宿失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "取消入住申请")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelApplication(
            @Parameter(description = "入住记录ID") @PathVariable String id,
            @Parameter(description = "取消原因") @RequestParam String reason) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.cancelApplication(id, reason, currentUser);
            if (success) {
                return Result.success();
            } else {
                return Result.error("取消申请失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "批量审批入住申请")
    @PostMapping("/batch-approve")
    public Result<Map<String, Object>> batchApprove(
            @RequestBody List<String> ids,
            @RequestParam Integer status,
            @RequestParam(required = false) String approvalRemark) {
        try {
            String currentUser = getCurrentUser();
            Map<String, Object> result = checkInService.batchApprove(ids, status, approvalRemark, currentUser);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取入住统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCheckInStatistics() {
        Map<String, Object> statistics = checkInService.getCheckInStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "根据学生ID获取入住记录")
    @GetMapping("/student/{studentId}")
    public Result<CheckInVO> getCheckInByStudentId(
            @Parameter(description = "学生ID") @PathVariable String studentId) {
        CheckInVO checkIn = checkInService.getCheckInByStudentId(studentId);
        return Result.success(checkIn);
    }

    @Operation(summary = "根据宿舍ID获取入住学生列表")
    @GetMapping("/dormitory/{dormitoryId}")
    public Result<List<CheckInVO>> getCheckInsByDormitoryId(
            @Parameter(description = "宿舍ID") @PathVariable String dormitoryId) {
        List<CheckInVO> checkIns = checkInService.getCheckInsByDormitoryId(dormitoryId);
        return Result.success(checkIns);
    }

    @Operation(summary = "根据床位ID获取入住记录")
    @GetMapping("/bed/{bedId}")
    public Result<CheckInVO> getCheckInByBedId(
            @Parameter(description = "床位ID") @PathVariable String bedId) {
        CheckInVO checkIn = checkInService.getCheckInByBedId(bedId);
        return Result.success(checkIn);
    }

    @Operation(summary = "获取可申请入住的学生列表（在校生且未入住）")
    @GetMapping("/available-students")
    public Result<List<AvailableStudentDTO>> getAvailableStudents(
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword) {
        List<AvailableStudentDTO> students = checkInService.getAvailableStudents(keyword);
        return Result.success(students);
    }

    @Operation(summary = "获取可用床位列表（有空闲床位的宿舍）")
    @GetMapping("/available-beds")
    public Result<List<AvailableBedDTO>> getAvailableBeds() {
        List<AvailableBedDTO> beds = checkInService.getAvailableBeds();
        return Result.success(beds);
    }
}