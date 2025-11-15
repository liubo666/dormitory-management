package com.dormitory.management.dto;

import lombok.Data;

/**
 * 访客登记分页查询参数
 */
@Data
public class VisitorPageDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 访客姓名
     */
    private String visitorName;

    /**
     * 访客手机号
     */
    private String visitorPhone;

    /**
     * 被访学生姓名
     */
    private String visitStudentName;

    /**
     * 被访学生学号
     */
    private String visitStudentNo;

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
     * 访问开始日期
     */
    private String startDate;

    /**
     * 访问结束日期
     */
    private String endDate;

    /**
     * 访客状态
     */
    private Integer status;
}