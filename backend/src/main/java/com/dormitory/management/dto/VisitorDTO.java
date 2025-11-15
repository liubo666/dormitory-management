package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 访客登记数据传输对象
 */
@Data
public class VisitorDTO {

    /**
     * 主键ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 访客姓名
     */
    private String visitorName;

    /**
     * 访客手机号
     */
    private String visitorPhone;

    /**
     * 访客身份证号
     */
    private String visitorIdCard;

    /**
     * 被访学生姓名
     */
    private String visitStudentName;

    /**
     * 被访学生学号
     */
    private String visitStudentNo;

    /**
     * 访问宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    /**
     * 访问事由
     */
    private String visitPurpose;

    /**
     * 预计访问时间
     */
    private String expectedVisitTime;

    /**
     * 实际到达时间
     */
    private String actualArrivalTime;

    /**
     * 离开时间
     */
    private String leaveTime;

    /**
     * 访客状态：0-待访问，1-访问中，2-已完成，3-已取消
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remarks;
}