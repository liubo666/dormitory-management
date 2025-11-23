package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 部分支付DTO
 */
@Data
@Schema(description = "部分支付请求DTO")
public class PartialPaymentDTO {

    @NotNull(message = "费用ID不能为空")
    @Schema(description = "费用ID", example = "1")
    private Long id;

    @NotNull(message = "支付金额不能为空")
    @Schema(description = "支付金额", example = "600.00")
    private BigDecimal paymentAmount;
}