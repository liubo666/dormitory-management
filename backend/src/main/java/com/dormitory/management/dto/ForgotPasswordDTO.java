package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 忘记密码请求DTO
 */
@Data
@Schema(description = "忘记密码请求")
public class ForgotPasswordDTO {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址", example = "user@example.com")
    private String email;
}