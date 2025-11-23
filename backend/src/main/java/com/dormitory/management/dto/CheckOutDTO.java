package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 退宿处理DTO
 */
@Data
@Schema(description = "退宿处理请求DTO")
public class CheckOutDTO {

    @NotBlank(message = "入住记录ID不能为空")
    @Schema(description = "入住记录ID", example = "123456")
    private String checkInId;

    @Schema(description = "退宿原因", example = "毕业离校")
    private String checkoutReason;
}