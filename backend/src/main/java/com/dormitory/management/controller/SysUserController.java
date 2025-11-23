package com.dormitory.management.controller;

import com.dormitory.management.common.Result;
import com.dormitory.management.dto.ForgotPasswordDTO;
import com.dormitory.management.dto.ResetPasswordDTO;
import com.dormitory.management.dto.LoginDTO;
import com.dormitory.management.dto.LoginVO;
import com.dormitory.management.service.SysUserService;
import com.dormitory.management.util.JwtUtil;
import com.dormitory.management.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            LoginVO loginVO = sysUserService.login(loginDTO);
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        try {
            sysUserService.logout();
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getCurrentUserInfo() {
        try {
            UserInfoVO userInfoVO = sysUserService.getCurrentUserInfo();
            return Result.success(userInfoVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新个人信息")
    @PostMapping("/updateInfo")
    public Result<UserInfoVO> updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO) {
        try {
            UserInfoVO updatedUserInfo = sysUserService.updateUserInfo(userInfoVO);
            return Result.success(updatedUserInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "刷新访问Token")
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestParam String refreshToken) {
        try {
            // 验证refreshToken是否有效
            if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                return Result.error("刷新Token无效");
            }

            // 获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            if (userId == null) {
                return Result.error("刷新Token解析失败");
            }

            // 获取用户信息
            var user = sysUserService.getById(userId);
            if (user == null || user.getStatus() != 1) {
                return Result.error("用户不存在或已禁用");
            }

            // 生成新的Token
            String newToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            LoginVO loginVO = new LoginVO();
            loginVO.setToken(newToken);
            loginVO.setRefreshToken(newRefreshToken);

            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error("刷新Token失败：" + e.getMessage());
        }
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@Validated @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        try {
            sysUserService.sendResetPasswordEmail(forgotPasswordDTO.getUsername(), forgotPasswordDTO.getEmail());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            sysUserService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getPassword());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}