package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dormitory.management.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别(1:男,2:女)
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色(admin,user)
     */
    private String role;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 头像
     */
    private String avatar;
}