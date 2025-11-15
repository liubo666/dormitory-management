package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访客登记实体类
 */
@Data
@TableName("visitor")
public class Visitor {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
    private Long roomId;

    /**
     * 访问宿舍号
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
     * 访问事由
     */
    private String visitPurpose;

    /**
     * 预计访问时间
     */
    private LocalDateTime expectedVisitTime;

    /**
     * 实际到达时间
     */
    private LocalDateTime actualArrivalTime;

    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;

    /**
     * 访客状态：0-待访问，1-访问中，2-已完成，3-已取消
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 登记人ID
     */
    private Long registrarId;

    /**
     * 登记人姓名
     */
    private String registrarName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}