package com.dormitory.management.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class RepairDTO {

    private Long id;

    /**
     * 宿舍ID
     */
    @NotNull(message = "宿舍ID不能为空")
    private Long roomId;

    /**
     * 报修学生ID
     */
    @NotNull(message = "报修学生ID不能为空")
    private Long studentId;

    /**
     * 报修标题
     */
    @NotBlank(message = "报修标题不能为空")
    private String title;

    /**
     * 报修描述
     */
    private String description;

    /**
     * 报修类型：water-水电，electric-电路，door-门窗，furniture-家具，other-其他
     */
    @NotBlank(message = "报修类型不能为空")
    private String type;

    /**
     * 优先级：1-低，2-中，3-高
     */
    private Integer priority;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 报修图片列表
     */
    private List<String> images;
}