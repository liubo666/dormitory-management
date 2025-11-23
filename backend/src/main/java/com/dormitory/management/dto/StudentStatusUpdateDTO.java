package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新学生状态DTO
 */
@Data
@Schema(description = "更新学生状态请求DTO")
public class StudentStatusUpdateDTO {

    @NotBlank(message = "学生ID不能为空")
    @Schema(description = "学生ID", example = "1001")
    private String studentId;

    @Schema(description = "状态", example = "1")
    private Integer status;
}