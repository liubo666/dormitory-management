package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学生分页查询DTO
 */
@Data
@Schema(description = "学生分页查询请求参数")
public class StudentPageDTO {

    @Schema(description = "当前页", example = "1", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别(0:女,1:男)")
    private Integer gender;

    @Schema(description = "学院")
    private String college;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "宿舍ID")
    private String dormitoryId;

    @Schema(description = "学生状态(0:休学,1:在校,2:毕业,3:退学)")
    private Integer status;
}