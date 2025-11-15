package com.dormitory.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 费用管理分页查询数据传输对象
 */
@Data
public class FeePageDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 费用类型(1:住宿费,2:水电费,3:网费,4:其他费用)
     */
    private Integer feeType;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生学号
     */
    private String studentNo;

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
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 支付状态(0:未支付,1:部分支付,2:已支付)
     */
    private Integer paymentStatus;

    /**
     * 收费员ID
     */
    private Long collectorId;

    /**
     * 收费员姓名
     */
    private String collectorName;

    /**
     * 开始日期
     */
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    private LocalDateTime endDate;
}