package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求DTO
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordDTO {

    @NotBlank(message = "重置令牌不能为空")
    @Schema(description = "重置令牌", example = "abc123def456")
    private String token;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    @Schema(description = "新密码", example = "newPassword123")
    private String password;
}