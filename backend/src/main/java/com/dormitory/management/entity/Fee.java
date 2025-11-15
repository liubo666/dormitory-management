package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 费用管理实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fee")
public class Fee implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 费用ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生学号
     */
    private String studentNo;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    /**
     * 宿舍号
     */
    private String roomNo;


    /**
     * 楼栋名称
     */
    private String buildingName;

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
     * 应付金额
     */
    private BigDecimal payableAmount;

    /**
     * 实付金额
     */
    private BigDecimal paidAmount;

    /**
     * 未付金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 支付状态(0:未支付,1:部分支付,2:已支付)
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 支付方式(1:现金,2:银行转账,3:微信,4:支付宝,5:校园卡)
     */
    private Integer paymentMethod;

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
    private LocalDateTime collectionTime;

    /**
     * 发票号码
     */
    private String invoiceNo;

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

    /**
     * 删除标识(0:未删除,1:已删除)
     */
    @TableLogic
    private Integer deleted;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    private  String description;

}