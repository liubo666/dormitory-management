package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepairVO {


    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 报修学生ID
     */
    private Long studentId;

    /**
     * 报修学生姓名
     */
    private String studentName;

    /**
     * 报修标题
     */
    private String title;

    /**
     * 报修描述
     */
    private String description;

    /**
     * 报修类型
     */
    private String type;

    /**
     * 报修类型文本
     */
    private String typeText;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 优先级文本
     */
    private String priorityText;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 报修时间
     */
    private LocalDateTime reportTime;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理人姓名
     */
    private String handlerName;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 报修图片列表
     */
    private List<String> imageList;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}