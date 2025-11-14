package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 宿舍楼分页查询DTO
 */
@Data
@Schema(description = "宿舍楼分页查询请求参数")
public class BuildingPageDTO {

    @Schema(description = "当前页", example = "1", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "楼栋号")
    private String buildingNo;

    @Schema(description = "楼栋名称")
    private String buildingName;

    @Schema(description = "状态(0:停用,1:启用)")
    private Integer status;
}