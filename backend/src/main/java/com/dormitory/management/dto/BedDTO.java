package com.dormitory.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 床位数据传输对象
 */
@Data
public class BedDTO {

    /**
     * 床位ID
     */
    private String id;

    /**
     * 宿舍ID
     */
    @NotBlank(message = "宿舍ID不能为空")
    private String dormitoryId;

    /**
     * 床位号
     */
    @NotBlank(message = "床位号不能为空")
    private String bedNo;

    /**
     * 床位状态(0:可用,1:已占用,2:维修中)
     */
    @NotNull(message = "床位状态不能为空")
    private Integer status;

    /**
     * 床位描述
     */
    private String description;
}