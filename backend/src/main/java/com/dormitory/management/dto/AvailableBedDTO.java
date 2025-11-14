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
     * 楼栋ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dormitoryId;

    /**
     * 宿舍号
     */
    private String dormitoryNo;

    /**
     * 可用床位列表
     */
    private List<BedInfoDTO> bedList;

    /**
     * 床位信息DTO
     */
    @Data
    public static class BedInfoDTO {
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
}