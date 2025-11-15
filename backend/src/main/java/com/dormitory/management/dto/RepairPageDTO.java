package com.dormitory.management.dto;

import lombok.Data;

@Data
public class RepairPageDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 宿舍号
     */
    private String roomNo;

    /**
     * 宿舍ID
     */
    private Long roomId;

    /**
     * 楼栋ID
     */
    private Long buildingId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 报修类型
     */
    private String type;

    /**
     * 状态：1-待处理，2-处理中，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 优先级：1-低，2-中，3-高
     */
    private Integer priority;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}