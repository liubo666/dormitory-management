package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 入住申请审批数据传输对象
 */
@Data
@Schema(description = "入住申请审批请求")
public class CheckInApprovalDTO {

    /**
     * 入住记录ID
     */
    @NotNull(message = "入住记录ID不能为空")
    @Schema(description = "入住记录ID", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 审批状态 (0-待审批, 1-已通过, 2-已拒绝) 1:申请中2:已入住,3已退宿,4:已拒绝
     */
    @NotNull(message = "审批状态不能为空")
    @Schema(description = "审批状态", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

    /**
     * 审批备注
     */
    @Schema(description = "审批备注", example = "审批通过，同意入住")
    private String approvalRemark;
}