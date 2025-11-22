package com.dormitory.management.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * 注册申请DTO
 */
@Data
public class RegistrationApplicationDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 10, message = "姓名长度不能超过10个字符")
    private String realName;

    /**
     * 性别(1:男,2:女)
     */
    @NotNull(message = "性别不能为空")
    @Min(value = 1, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Integer gender;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 管理员工号
     */
    @NotBlank(message = "管理员工号不能为空")
    private String adminEmployeeNo;
}