package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 分配宿舍DTO
 */
@Data
@Schema(description = "分配宿舍请求DTO")
public class DormitoryAssignDTO {

    @NotBlank(message = "入住记录ID不能为空")
    @Schema(description = "入住记录ID", example = "123456")
    private String checkInId;

    @NotBlank(message = "宿舍ID不能为空")
    @Schema(description = "宿舍ID", example = "1001")
    private String dormitoryId;

    @NotBlank(message = "床位ID不能为空")
    @Schema(description = "床位ID", example = "2001")
    private String bedId;

    @NotBlank(message = "床位号不能为空")
    @Schema(description = "床位号", example = "A-101")
    private String bedNo;
}