package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 床位视图对象
 */
@Data
public class BedVO {

    /**
     * 床位ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dormitoryId;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 床位状态(0:可用,1:已占用,2:维修中)
     */
    private Integer status;

    /**
     * 床位状态文本
     */
    private String statusText;

    /**
     * 床位描述
     */
    private String description;



    /**
     * 是否被占用（用于前端判断）
     */
    private Boolean isOccupied;
}