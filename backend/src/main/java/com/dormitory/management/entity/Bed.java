package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 宿舍床位实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bed")
public class Bed {

    /**
     * 床位ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 宿舍ID
     */
    @TableField("dormitory_id")
    private Long dormitoryId;

    /**
     * 床位号
     */
    @TableField("bed_no")
    private String bedNo;

    /**
     *床位状态(1:可用,2:已占用 3预占用
     */
    @TableField("status")
    private Integer status;

    /**
     * 床位描述
     */
    @TableField("description")
    private String description;



    /**
     * 学号
     */
    @TableField("student_no")
    private String studentNo;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

}