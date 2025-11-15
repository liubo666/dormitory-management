package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "批量审批入住申请请求")
public class BatchCheckInApprovalDTO implements Serializable {

    /**
     * 入住记录ID列表
     */
    @NotEmpty(message = "入住记录ID列表不能为空")
    @Schema(description = "入住记录ID列表", example = "[1, 2, 3]")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> ids;

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
