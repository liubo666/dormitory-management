package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 入住分配视图对象
 */
@Data
public class CheckInVO {

    /**
     * 入住记录ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 学生ID
     */
    private String studentId;

    /**
     * 学生学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生性别
     */
    private Integer studentGender;

    /**
     * 学生性别文本
     */
    private String studentGenderText;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 宿舍ID
     */
    private String dormitoryId;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 床位ID
     */
    private String bedId;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 入住日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    /**
     * 预计退宿日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedCheckoutDate;

    /**
     * 实际退宿日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualCheckoutDate;

    /**
     * 入住状态(0:申请中,1:已入住,2:已退宿,3:已拒绝)
     */
    private Integer status;

    /**
     * 入住状态文本
     */
    private String statusText;

    /**
     * 申请原因
     */
    private String applicationReason;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;

    /**
     * 审批备注
     */
    private String approvalRemark;

    /**
     * 退宿原因
     */
    private String checkoutReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}