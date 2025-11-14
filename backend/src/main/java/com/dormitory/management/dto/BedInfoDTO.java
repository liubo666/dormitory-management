package com.dormitory.management.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class BedInfoDTO implements Serializable {

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 床位状态(0:可用,1:已占用,2:维修中)
     */
    private Integer status;

    /**
     * 床位描述
     */
    private String description;



    /**
     * 是否被占用（用于前端判断）
     */
    private Boolean isOccupied;


    /**
     * 床位状态(0:可用,1:已占用,2:维修中)
     */
    private String statusText;
}
