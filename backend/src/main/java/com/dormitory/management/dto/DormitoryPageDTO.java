package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 宿舍分页查询DTO
 */
@Data
@Schema(description = "宿舍分页查询请求参数")
public class DormitoryPageDTO {

    @Schema(description = "当前页", example = "1", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "楼栋ID（保留用于向后兼容）")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

  @Schema(description = "楼栋号")
    private String buildingNo;

  @Schema(description = "房间号")
    private String roomNo;

    @Schema(description = "楼层")
    private Integer floorNumber;

    @Schema(description = "状态(0:已满,1:可用,2:维护中)")
    private Integer status;
}