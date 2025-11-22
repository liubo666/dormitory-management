package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dormitory.management.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 注册申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("registration_application")
public class RegistrationApplication extends BaseEntity {

    /**
     * 申请编号
     */
    private String applicationNo;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密后）
     */
    private String password;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 性别(1:男,2:女)
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 管理员工号
     */
    private String adminEmployeeNo;

    /**
     * 申请状态(0:待审批,1:已通过,2:已驳回)
     */
    private Integer status;

    /**
     * 审批人ID
     */
    private Long approvedBy;

    /**
     * 审批时间
     */
    private LocalDateTime approvedTime;

    /**
     * 驳回原因
     */
    private String rejectionReason;

    /**
     * 审批token（用于邮件审批链接）
     */
    private String approvalToken;

    /**
     * token过期时间
     */
    private LocalDateTime tokenExpireTime;
}