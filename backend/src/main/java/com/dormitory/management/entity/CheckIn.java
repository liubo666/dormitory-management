package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住分配实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("check_in")
public class CheckIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入住记录ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;


    /**
     * 床位ID
     */
    @TableField("bed_id")
    private Long bedId;





    /**
     * 床位ID
     */
    @TableField("bed_no")
    private String bedNo;

    /**
     * 床位ID
     */
    @TableField("dormitory_id")
    private Long dormitoryId;



    /**
     * 入住日期
     */
    @TableField("check_in_date")
    private LocalDate checkInDate;

    /**
     * 预计退宿日期
     */
    @TableField("expected_checkout_date")
    private LocalDate expectedCheckoutDate;

    /**
     * 实际退宿日期
     */
    @TableField("actual_checkout_date")
    private LocalDate actualCheckoutDate;

    /**
     * 入住状态(1:申请中,2:已入住,3:已退宿,4:已拒绝)
     */
    @TableField("status")
    private Integer status;

    /**
     * 申请原因
     */
    @TableField("application_reason")
    private String applicationReason;

    /**
     * 审批人
     */
    @TableField("approver")
    private String approver;

    /**
     * 审批时间
     */
    @TableField("approval_time")
    private LocalDateTime approvalTime;

    /**
     * 审批备注
     */
    @TableField("approval_remark")
    private String approvalRemark;

    /**
     * 退宿原因
     */
    @TableField("checkout_reason")
    private String checkoutReason;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 是否删除(0:未删除,1:已删除)
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;



    @TableField("building_name")
    private  String buildingName;

    @TableField("room_no")
    private  String roomNo;

}