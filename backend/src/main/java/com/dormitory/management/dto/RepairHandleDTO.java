package com.dormitory.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class RepairHandleDTO {

    /**
     * 报修ID
     */
    @NotNull(message = "报修ID不能为空")
    private Long id;

    /**
     * 处理状态：2-处理中，3-已完成，4-已取消
     */
    @NotNull(message = "处理状态不能为空")
    private Integer status;

    /**
     * 处理备注
     */
    private String handleRemark;
}