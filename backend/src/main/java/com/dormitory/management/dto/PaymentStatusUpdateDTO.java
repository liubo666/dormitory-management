package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新支付状态DTO
 */
@Data
@Schema(description = "更新支付状态请求DTO")
public class PaymentStatusUpdateDTO {

    @NotNull(message = "费用ID不能为空")
    @Schema(description = "费用ID", example = "1")
    private Long id;

    @NotNull(message = "支付状态不能为空")
    @Schema(description = "支付状态", example = "2")
    private Integer paymentStatus;

    @Schema(description = "已支付金额", example = "1200.00")
    private BigDecimal paidAmount;
}