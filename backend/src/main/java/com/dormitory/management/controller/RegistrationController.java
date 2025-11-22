package com.dormitory.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.RegistrationApplicationDTO;
import com.dormitory.management.service.RegistrationApplicationService;
import com.dormitory.management.vo.RegistrationApplicationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 注册申请控制器
 */
@Tag(name = "注册申请管理", description = "注册申请相关接口")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationApplicationService registrationApplicationService;

    @Operation(summary = "提交注册申请")
    @PostMapping("/apply")
    public Result<String> submitApplication(@Validated @RequestBody RegistrationApplicationDTO dto) {
        try {
            String applicationNo = registrationApplicationService.submitApplication(dto);
            return Result.success(String.format("注册申请提交成功，请等待%s管理员审批",applicationNo));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "验证管理员工号")
    @GetMapping("/validate-admin/{employeeNo}")
    public Result<Boolean> validateAdminEmployeeNo(
            @Parameter(description = "管理员工号") @PathVariable String employeeNo) {
        try {
            boolean isValid = registrationApplicationService.validateAdminEmployeeNo(employeeNo);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "分页查询注册申请（管理员）")
    @GetMapping("/admin/applications")
    public Result<IPage<RegistrationApplicationVO>> getApplicationPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "状态：0-待审批，1-已通过，2-已驳回") @RequestParam(required = false) Integer status) {
        try {

            IPage<RegistrationApplicationVO> result = registrationApplicationService.getApplicationPage( status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "根据审批token获取申请详情")
    @GetMapping("/application/{token}")
    public Result<RegistrationApplicationVO> getApplicationByToken(
            @Parameter(description = "审批token") @PathVariable String token) {
        try {
            RegistrationApplicationVO application = registrationApplicationService.getApplicationByToken(token);
            if (application == null) {
                return Result.error("无效的审批链接");
            }
            return Result.success(application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "审批通过")
    @PostMapping("/admin/approve/{token}")
    public Result<Void> approveApplication(
            @Parameter(description = "审批token") @PathVariable String token) {
        try {

            // 验证管理员是否有权限审批此申请
            registrationApplicationService.approveApplication(token);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "审批驳回")
    @PostMapping("/admin/reject/{token}")
    public Result<Void> rejectApplication(
            @Parameter(description = "审批token") @PathVariable String token,
            @Parameter(description = "驳回原因") @RequestParam String reason) {
        try {

            // 验证管理员是否有权限审批此申请
            registrationApplicationService.rejectApplication(token, reason);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}