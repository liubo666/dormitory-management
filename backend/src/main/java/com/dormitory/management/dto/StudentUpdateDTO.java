package com.dormitory.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新学生信息DTO
 */
@Data
@Schema(description = "更新学生信息请求DTO")
public class StudentUpdateDTO {

    @NotBlank(message = "学生ID不能为空")
    @Schema(description = "学生ID", example = "1001")
    private String id;

    @Schema(description = "学号", example = "20240001")
    private String studentNo;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "性别", example = "男")
    private String gender;

    @Schema(description = "年龄", example = "20")
    private Integer age;

    @Schema(description = "学院", example = "计算机学院")
    private String college;

    @Schema(description = "专业", example = "软件工程")
    private String major;

    @Schema(description = "年级", example = "2024")
    private String grade;

    @Schema(description = "班级", example = "软件工程1班")
    private String className;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "身份证号", example = "110101200001011234")
    private String idCard;

    @Schema(description = "家庭地址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "紧急联系人", example = "李四")
    private String emergencyContact;

    @Schema(description = "紧急联系人电话", example = "13900139000")
    private String emergencyPhone;

    @Schema(description = "宿舍ID", example = "1001")
    private Long dormitoryId;

    @Schema(description = "床位号", example = "A-101-1")
    private String bedNo;

    @Schema(description = "入住状态", example = "1")
    private Integer checkInStatus;
}