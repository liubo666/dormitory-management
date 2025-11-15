package com.dormitory.management.dto;

import lombok.Data;

/**
 * 宿舍搜索数据传输对象
 */
@Data
public class DormitorySearchDTO {

    /**
     * 关键字（支持宿舍号、宿舍楼名称模糊查询）
     */
    private String keyword;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;

    /**
     * 宿舍楼ID（可选筛选条件）
     */
    private Long buildingId;

    /**
     * 宿舍楼编号（可选筛选条件）
     */
    private String buildingNo;

    /**
     * 宿舍状态（可选筛选条件，1:可用，0:不可用）
     */
    private Integer status;
}