package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 费用管理数据传输对象
 */
@Data
public class FeeDTO {

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
     * 费用名称
     */
    private String feeName;

    /**
     * 费用金额
     */
    private BigDecimal amount;

    /**
     * 收费周期(1:月度,2:季度,3:年度,4:一次性)
     */
    private Integer billingCycle;

    /**
     * 学生ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long studentId;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    /**
     * 费用开始时间
     */
    private Date startTime;

    /**
     * 费用结束时间
     */
    private Date endTime;

    /**
     * 支付状态(0:未支付,1:部分支付,2:已支付)
     */
    private Integer paymentStatus;

    /**
     * 支付方式(1:现金,2:银行转账,3:微信,4:支付宝,5:校园卡)
     */
    private Integer paymentMethod;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 备注
     */
    private String remark;

    private  String description;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;
}