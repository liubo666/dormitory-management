package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 卫生检查实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_inspection")
public class Inspection {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 宿舍ID
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 检查日期
     */
    @TableField("inspection_date")
    private LocalDateTime inspectionDate;

    /**
     * 卫生分数
     */
    @TableField("score")
    private Integer score;

    /**
     * 等级：excellent-优秀，good-良好，pass-合格，fail-不合格
     */
    @TableField("level")
    private String level;

    /**
     * 检查人ID
     */
    @TableField("inspector_id")
    private Long inspectorId;

    /**
     * 检查人姓名
     */
    @TableField("inspector_name")
    private String inspectorName;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 详细问题描述
     */
    @TableField("issues")
    private String issues;

    /**
     * 检查图片列表（用逗号分隔）
     */
    @TableField("images")
    private String images;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}