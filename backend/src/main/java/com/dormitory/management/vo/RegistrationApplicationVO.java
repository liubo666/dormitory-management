package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 注册申请VO
 */
@Data
@Schema(description = "注册申请信息")
public class RegistrationApplicationVO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "申请编号")
    private String applicationNo;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "性别(1:男,2:女)")
    private Integer gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "管理员工号")
    private String adminEmployeeNo;

    @Schema(description = "申请状态(0:待审批,1:已通过,2:已驳回)")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusText;

    @Schema(description = "审批人")
    private String approvedByName;

    @Schema(description = "审批时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedTime;

    @Schema(description = "驳回原因")
    private String rejectionReason;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}