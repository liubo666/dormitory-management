package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 宿舍搜索结果视图对象
 */
@Data
public class DormitorySearchVO {

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;



    /**
     * 宿舍楼编号
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String buildingNo;

    /**
     * 宿舍楼名称
     */
    private String buildingName;

    /**
     * 宿舍号
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String roomNo;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 床位数
     */
    private Integer bedCount;

    /**
     * 已住人数
     */
    private Integer occupiedCount;

    /**
     * 宿舍类型（1:男生宿舍，2:女生宿舍）
     */
    private Integer roomType;

    /**
     * 宿舍状态（1:可用，0:不可用）
     */
    private Integer status;

    /**
     * 宿舍描述
     */
    private String description;

    /**
     * 显示标签（宿舍楼名称 宿舍号）
     */
    private String label;

    /**
     * 选项值
     */
    private Object value;
}