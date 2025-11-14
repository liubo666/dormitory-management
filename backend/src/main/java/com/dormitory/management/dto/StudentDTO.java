package com.dormitory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 学生数据传输对象
 */
@Data
@Schema(description = "学生信息")
public class StudentDTO {

    @Schema(description = "学生ID")
    private String id;

    @NotBlank(message = "学号不能为空")
//    @Pattern(regexp = "^[0-9]{10,20}$", message = "学号格式不正确")
    @Schema(description = "学号", example = "2024001001")
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    @Schema(description = "姓名", example = "张三")
    private String name;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别(0:女,1:男)", example = "1")
    private Integer gender;

    @NotNull(message = "出生日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "出生日期", example = "2000-01-01")
    private LocalDate birthDate;

    @Pattern(regexp = "^[0-9Xx]{18}$", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101200001011234")
    private String idCard;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "student@example.com")
    private String email;

    @NotBlank(message = "学院不能为空")
    @Size(max = 100, message = "学院名称长度不能超过100个字符")
    @Schema(description = "学院", example = "计算机科学与技术学院")
    private String college;

    @NotBlank(message = "专业不能为空")
    @Size(max = 100, message = "专业名称长度不能超过100个字符")
    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    @Size(max = 100, message = "班级名称长度不能超过100个字符")
    @Schema(description = "班级", example = "计科2001班")
    private String className;

    @Size(max = 20, message = "年级长度不能超过20个字符")
    @Schema(description = "年级", example = "2020级")
    private String grade;

    @Schema(description = "入学日期", example = "2020-09-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;

    @Schema(description = "毕业日期", example = "2024-06-30")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    @Schema(description = "宿舍ID")
    private String dormitoryId;

    @Size(max = 20, message = "床位号长度不能超过20个字符")
    @Schema(description = "床位号", example = "1号床")
    private String bedNo;

    @NotNull(message = "学生状态不能为空")
    @Schema(description = "学生状态(0:休学,1:在校,2:毕业,3:退学)", example = "1")
    private Integer status;

    @Size(max = 200, message = "家庭住址长度不能超过200个字符")
    @Schema(description = "家庭住址")
    private String homeAddress;

    @Size(max = 50, message = "紧急联系人姓名长度不能超过50个字符")
    @Schema(description = "紧急联系人")
    private String emergencyContact;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    @Schema(description = "紧急联系人电话", example = "13900139000")
    private String emergencyPhone;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注")
    private String remark;
}