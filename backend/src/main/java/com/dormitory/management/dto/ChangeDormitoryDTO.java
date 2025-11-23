package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 调换宿舍DTO
 */
@Data
@Schema(description = "调换宿舍请求DTO")
public class ChangeDormitoryDTO {

    @NotBlank(message = "学生ID不能为空")
    @Schema(description = "学生ID", example = "1001")
    private String studentId;

    @NotBlank(message = "新宿舍ID不能为空")
    @Schema(description = "新宿舍ID", example = "2002")
    private String newDormitoryId;

    @NotBlank(message = "新床位号不能为空")
    @Schema(description = "新床位号", example = "B-102-2")
    private String newBedNo;

    @Schema(description = "调换原因", example = "申请调换到安静宿舍")
    private String reason;
}