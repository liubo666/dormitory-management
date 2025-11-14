package com.dormitory.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 包含床位信息的宿舍数据传输对象
 */
@Data
public class DormitoryWithBedsDTO {

    /**
     * 宿舍ID
     */
    private String id;

    /**
     * 楼栋ID
     */
    @NotBlank(message = "楼栋ID不能为空")
    private String buildingId;

    /**
     * 房间号
     */
    @NotBlank(message = "房间号不能为空")
    private String roomNo;

    /**
     * 楼层
     */
    @NotNull(message = "楼层不能为空")
    private Integer floorNumber;

    /**
     * 床位数
     */
    @NotNull(message = "床位数不能为空")
    private Integer bedCount;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态(0:已满,1:可用,2:维护中)
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 床位列表
     */
    private List<BedDTO> beds;
}