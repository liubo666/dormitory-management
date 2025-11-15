package com.dormitory.management.dto;

import lombok.Data;

/**
 * 学生搜索数据传输对象
 */
@Data
public class StudentSearchDTO {

    /**
     * 关键字（支持学生姓名、学号模糊查询）
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
}