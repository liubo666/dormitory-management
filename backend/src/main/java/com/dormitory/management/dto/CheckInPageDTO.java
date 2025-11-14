package com.dormitory.management.dto;

import lombok.Data;

/**
 * 入住分配分页查询DTO
 */
@Data
public class CheckInPageDTO {

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 学生ID
     */
    private String studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 宿舍ID
     */
    private String dormitoryId;

    /**
     * 楼栋ID
     */
    private String buildingId;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 入住状态(0:申请中,1:已入住,2:已退宿,3:已拒绝)
     */
    private Integer status;

    /**
     * 入住开始日期
     */
    private String checkInStartDate;

    /**
     * 入住结束日期
     */
    private String checkInEndDate;

    /**
     * 申请开始日期
     */
    private String applyStartDate;

    /**
     * 申请结束日期
     */
    private String applyEndDate;
}