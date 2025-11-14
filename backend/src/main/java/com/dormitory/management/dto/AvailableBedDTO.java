package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * 可用床位DTO
 */
@Data
public class AvailableBedDTO {

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dormitoryId;


    /**
     * 楼栋号
     */
    private String buildingNo;

    /**
     * 楼栋名称
     */
    private String buildingName;



    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 楼层
     */
    private Integer floorNumber;

    /**
     * 可用床位列表
     */
    private List<BedInfoDTO> bedList;


}