package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住分配数据传输对象
 */
@Data
public class CheckInDTO {

    /**
     * 入住记录ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long studentId;

    /**
     * 宿舍ID
     */
    @NotNull(message = "宿舍ID不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dormitoryId;

    /**
     * 床位ID
     */
    @NotNull(message = "床位ID不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long bedId;

    /**
     * 床位号
     */
    @NotBlank(message = "床位号不能为空")
    private String bedNo;

    /**
     * 入住日期
     */
    @NotNull(message = "入住日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkInDate;

    /**
     * 预计退宿日期
     */
    private LocalDateTime expectedCheckoutDate;


    /**
     * 申请原因
     */
    private String applicationReason;

    /**
     * 审批人
     */
    private String approver;

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
}