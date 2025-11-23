package com.dormitory.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.*;
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
    @PostMapping("/approve")
    public Result<Void> approveApplication(@Validated @RequestBody CheckInApprovalDTO approvalDTO) {
        try {
            boolean success = checkInService.approveCheckIn(
                    approvalDTO);
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
    @PostMapping("/assign-dormitory")
    public Result<Void> assignDormitory(@Validated @RequestBody DormitoryAssignDTO assignDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.assignDormitory(
                    assignDTO.getCheckInId(),
                    assignDTO.getDormitoryId(),
                    assignDTO.getBedId(),
                    assignDTO.getBedNo(),
                    currentUser);
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
    @PostMapping("/checkout")
    public Result<Void> checkout(@Validated @RequestBody CheckOutDTO checkoutDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.checkout(
                    checkoutDTO.getCheckInId(),
                    checkoutDTO.getCheckoutReason(),
                    currentUser);
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
    @PostMapping("/cancel")
    public Result<Void> cancelApplication(@Validated @RequestBody CancelApplicationDTO cancelDTO) {
        try {
            String currentUser = getCurrentUser();
            boolean success = checkInService.cancelApplication(
                    cancelDTO.getCheckInId(),
                    cancelDTO.getReason(),
                    currentUser);
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
           @RequestBody BatchCheckInApprovalDTO  dto) {
        try {
            Map<String, Object> result = checkInService.batchApprove(dto);
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
    @PostMapping("/available-students")
    public Result<Page<AvailableStudentDTO>> getAvailableStudents(@RequestBody CheckInResDTO dto) {
        return Result.success(checkInService.getAvailableStudents(dto));
    }

    @Operation(summary = "获取可用床位列表（有空闲床位的宿舍）")
    @GetMapping("/available-beds")
    public Result<List<AvailableBedDTO>> getAvailableBeds(@RequestParam(value = "keyword",required = false) String keyword) {
        List<AvailableBedDTO> beds = checkInService.getAvailableBeds(keyword);
        return Result.success(beds);
    }
}