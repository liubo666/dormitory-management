package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 学生搜索结果视图对象
 */
@Data
public class StudentSearchVO {

    /**
     * 学生ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 学生学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 所属宿舍楼ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    /**
     * 所属宿舍楼名称
     */
    private String buildingName;

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    /**
     * 宿舍号
     */
    private String roomNo;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 显示标签（学号 - 姓名）
     */
    private String label;

    /**
     * 选项值
     */
    private Object value;
}