package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 更新宿舍DTO
 */
@Data
@Schema(description = "更新宿舍请求DTO")
public class DormitoryUpdateDTO {

    @NotNull(message = "宿舍ID不能为空")
    @Schema(description = "宿舍ID", example = "1")
    private Long id;

    @Schema(description = "楼栋ID", example = "1")
    private Long buildingId;

    @Schema(description = "房间号", example = "101")
    private String roomNo;

    @Schema(description = "床位数量", example = "4")
    private Integer bedCount;

    @Schema(description = "已用床位", example = "2")
    private Integer usedBeds;

    @Schema(description = "可用床位", example = "2")
    private Integer availableBeds;

    @Schema(description = "宿舍类型", example = "男生宿舍")
    private String roomType;

    @Schema(description = "宿舍状态", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "朝南宿舍")
    private String remark;
}