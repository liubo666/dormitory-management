package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 学生退宿DTO
 */
@Data
@Schema(description = "学生退宿请求DTO")
public class StudentCheckoutDTO {

    @NotBlank(message = "学生ID不能为空")
    @Schema(description = "学生ID", example = "1001")
    private String studentId;

    @Schema(description = "退宿原因", example = "毕业离校")
    private String reason;
}