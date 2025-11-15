package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 费用管理视图对象
 */
@Data
public class FeeVO {

    /**
     * 费用ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 费用类型(1:住宿费,2:水电费,3:网费,4:其他费用)
     */
    private Integer feeType;

    /**
     * 费用类型名称
     */
    private String feeTypeName;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 费用描述
     */
    private String description;

    /**
     * 费用金额
     */
    private BigDecimal amount;

    /**
     * 计费周期(1:按月,2:按学期,3:按年,4:一次性)
     */
    private Integer billingCycle;

    /**
     * 计费周期名称
     */
    private String billingCycleName;

    /**
     * 学生ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long studentId;

    /**
     * 学生学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    /**
     * 宿舍楼编号
     */
    private String buildingNo;

    /**
     * 宿舍楼名称
     */
    private String buildingName;

    /**
     * 宿舍号
     */
    private String roomNo;

    /**
     * 费用开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 费用结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付状态(0:未支付,1:部分支付,2:已支付)
     */
    private Integer paymentStatus;

    /**
     * 支付状态名称
     */
    private String paymentStatusName;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 未付金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 收费员ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long collectorId;

    /**
     * 收费员姓名
     */
    private String collectorName;

    /**
     * 收费时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

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