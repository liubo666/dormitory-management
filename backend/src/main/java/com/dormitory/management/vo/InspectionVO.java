package com.dormitory.management.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 卫生检查视图对象
 */
@Data
public class InspectionVO {

    /**
     * 检查记录ID
     */
    private Long id;

    /**
     * 宿舍ID
     */
    private Long roomId;

    /**
     * 宿舍号
     */
    private String roomNo;

    /**
     * 楼栋ID
     */
    private Long buildingId;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 检查日期
     */
    private LocalDateTime inspectionDate;

    /**
     * 卫生分数
     */
    private Integer score;

    /**
     * 等级
     */
    private String level;

    /**
     * 等级文本
     */
    private String levelText;

    /**
     * 检查人ID
     */
    private Long inspectorId;

    /**
     * 检查人姓名
     */
    private String inspectorName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 详细问题描述
     */
    private String issues;

    /**
     * 检查图片列表
     */
    private List<String> imageList;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}