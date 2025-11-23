package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新费用DTO
 */
@Data
@Schema(description = "更新费用请求DTO")
public class FeeUpdateDTO {

    @NotNull(message = "费用ID不能为空")
    @Schema(description = "费用ID", example = "1")
    private Long id;

    @Schema(description = "学生ID", example = "1001")
    private Long studentId;

    @Schema(description = "宿舍ID", example = "2001")
    private Long dormitoryId;

    @Schema(description = "费用类型", example = "住宿费")
    private String feeType;

    @NotNull(message = "费用金额不能为空")
    @Schema(description = "费用金额", example = "1200.00")
    private BigDecimal amount;

    @Schema(description = "费用年度", example = "2024")
    private String feeYear;

    @Schema(description = "费用月份", example = "9")
    private String feeMonth;

    @Schema(description = "支付状态", example = "0")
    private Integer paymentStatus;

    @Schema(description = "已支付金额", example = "600.00")
    private BigDecimal paidAmount;

    @Schema(description = "备注", example = "9月份住宿费")
    private String remark;
}