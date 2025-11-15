package com.dormitory.management.dto;

import lombok.Data;

/**
 * 卫生检查分页查询参数
 */
@Data
public class InspectionPageDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 宿舍ID
     */
    private Long roomId;

    /**
     * 宿舍号
     */
    private String roomNo;

    /**
     * 楼栋ID
     */
    private Long buildingId;

    /**
     * 检查开始日期
     */
    private String startDate;

    /**
     * 检查结束日期
     */
    private String endDate;

    /**
     * 等级
     */
    private String level;
}