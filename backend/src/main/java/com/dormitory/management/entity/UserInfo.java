package com.dormitory.management.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 用户信息实体类（用于ThreadLocal存储）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色ID（如果有）
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 是否启用
     */
    private Boolean enabled;
}