package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
@Data
public class BedInfoV1DTO implements Serializable {


    /**
     * 床位ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dormitoryId;
}
