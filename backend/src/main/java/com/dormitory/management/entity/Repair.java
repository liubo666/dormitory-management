package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("repair")
public class Repair {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 宿舍ID
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 宿舍号
     */
    @TableField("room_no")
    private String roomNo;

    /**
     * 楼栋名称
     */
    @TableField("building_name")
    private String buildingName;

    /**
     * 报修学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 报修学生姓名
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 报修标题
     */
    @TableField("title")
    private String title;

    /**
     * 报修描述
     */
    @TableField("description")
    private String description;

    /**
     * 报修类型：water-水电，electric-电路，door-门窗，furniture-家具，other-其他
     */
    @TableField("type")
    private String type;

    /**
     * 优先级：1-低，2-中，3-高
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 状态：1-待处理，2-处理中，3-已完成，4-已取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 报修时间
     */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
     * 处理时间
     */
    @TableField("handle_time")
    private LocalDateTime handleTime;

    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 处理人ID
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 处理人姓名
     */
    @TableField("handler_name")
    private String handlerName;

    /**
     * 处理备注
     */
    @TableField("handle_remark")
    private String handleRemark;

    /**
     * 报修图片，多个图片用逗号分隔
     */
    @TableField("images")
    private String images;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

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

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}