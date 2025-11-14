package com.dormitory.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 宿舍楼响应VO
 */
@Data
@Schema(description = "宿舍楼响应数据")
public class BuildingVO {

    @Schema(description = "楼栋ID", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @Schema(description = "楼栋号", example = "A1")
    private String buildingNo;

    @Schema(description = "楼栋名称", example = "A1栋")
    private String buildingName;

    @Schema(description = "楼层数", example = "6")
    private Integer floorCount;

    @Schema(description = "描述", example = "男生宿舍楼")
    private String description;

    @Schema(description = "状态(0:停用,1:启用)", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "启用")
    private String statusText;

    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "创建人", example = "admin")
    private String createBy;

    @Schema(description = "更新人", example = "admin")
    private String updateBy;
}