package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 宿舍楼请求DTO
 */
@Data
@Schema(description = "宿舍楼请求参数")
public class BuildingDTO {

    @Schema(description = "楼栋ID", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @NotBlank(message = "楼栋号不能为空")
    @Schema(description = "楼栋号", example = "A1")
    private String buildingNo;

    @NotBlank(message = "楼栋名称不能为空")
    @Schema(description = "楼栋名称", example = "A1栋")
    private String buildingName;

    @NotNull(message = "楼层数不能为空")
    @Min(value = 1, message = "楼层数不能少于1层")
    @Max(value = 30, message = "楼层数不能超过30层")
    @Schema(description = "楼层数", example = "6")
    private Integer floorCount;

    @Schema(description = "描述", example = "男生宿舍楼")
    private String description;

//    @NotNull(message = "状态不能为空")
//    @Schema(description = "状态(0:停用,1:启用)", example = "1")
//    private Integer status;
}