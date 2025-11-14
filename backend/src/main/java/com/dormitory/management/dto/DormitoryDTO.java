package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;


/**
 * 宿舍数据传输对象
 */
@Data
public class DormitoryDTO {

    /**
     * 宿舍ID（更新时需要）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;


    /**
     * 楼栋号
     */
    @NotBlank(message = "楼栋号不能为空")
    private String buildingNo;

    /**
     * 楼栋名称
     */
    @NotBlank(message = "楼栋名称不能为空")
    private String buildingName;



    /**
     * 房间号
     */
    @NotBlank(message = "房间号不能为空")
    private String roomNo;

    /**
     * 楼层
     */
    @NotNull(message = "楼层不能为空")
    @Min(value = 1, message = "楼层不能小于1")
    @Max(value = 30, message = "楼层不能大于30")
    private Integer floorNumber;


    @NotNull(message = "床位信息")
    private List<BedInfoDTO> bedInfos;
    /**
     * 描述
     */
    private String description;
}