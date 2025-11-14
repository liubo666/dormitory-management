package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 宿舍分配DTO
 */
@Data
@Schema(description = "宿舍分配信息")
public class DormitoryAssignmentDTO {

    @NotBlank(message = "学生ID不能为空")
    @Schema(description = "学生ID")
    private String studentId;

    @NotBlank(message = "宿舍ID不能为空")
    @Schema(description = "宿舍ID")
    private String dormitoryId;

    @NotBlank(message = "床位号不能为空")
    @Schema(description = "床位号", example = "1号床")
    private String bedNo;

    @Schema(description = "分配原因")
    private String reason;
}