package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 取消入住申请DTO
 */
@Data
@Schema(description = "取消入住申请请求DTO")
public class CancelApplicationDTO {

    @NotBlank(message = "入住记录ID不能为空")
    @Schema(description = "入住记录ID", example = "123456")
    private String checkInId;

    @NotBlank(message = "取消原因不能为空")
    @Schema(description = "取消原因", example = "个人原因")
    private String reason;
}