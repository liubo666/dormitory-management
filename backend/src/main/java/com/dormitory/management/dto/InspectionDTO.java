package com.dormitory.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 卫生检查数据传输对象
 */
@Data
public class InspectionDTO {

    /**
     * 检查记录ID
     */
    private Long id;

    /**
     * 宿舍ID
     */
    @NotNull(message = "宿舍ID不能为空")
    private Long roomId;

    /**
     * 检查日期
     */
    @NotNull(message = "检查日期不能为空")
    private String inspectionDate;

    /**
     * 卫生分数
     */
    @NotNull(message = "卫生分数不能为空")
    private Integer score;

    /**
     * 等级：excellent-优秀，good-良好，pass-合格，fail-不合格
     */
    @NotNull(message = "等级不能为空")
    private String level;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 详细问题描述
     */
    private String issues;

    /**
     * 检查图片列表
     */
    private List<String> images;
}