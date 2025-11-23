package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 更新宿舍楼DTO
 */
@Data
@Schema(description = "更新宿舍楼请求DTO")
public class BuildingUpdateDTO {

    @NotNull(message = "楼栋ID不能为空")
    @Schema(description = "楼栋ID", example = "1")
    private Long id;

    @Schema(description = "楼栋编号", example = "A栋")
    private String buildingNo;

    @Schema(description = "楼栋名称", example = "学生公寓A栋")
    private String buildingName;

    @Schema(description = "楼层数量", example = "6")
    private Integer floorCount;

    @Schema(description = "房间数量", example = "120")
    private Integer roomCount;

    @Schema(description = "楼栋状态", example = "1")
    private Integer status;

    @Schema(description = "楼栋描述", example = "男生宿舍楼")
    private String description;
}