package com.dormitory.management.enums;

import lombok.Getter;

/**
 * 入住状态枚举
 */
@Getter
public enum CheckInStatusEnum {

    /**
     * 申请中
     */
    APPLYING(1, "申请中"),

    /**
     * 已入住
     */
    CHECKED_IN(2, "已入住"),

    /**
     * 已退宿
     */
    CHECKED_OUT(3, "已退宿"),

    /**
     * 已拒绝
     */
    REJECTED(4, "已拒绝");

    private final Integer code;
    private final String description;

    CheckInStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举值
     */
    public static CheckInStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CheckInStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态码获取描述
     *
     * @param code 状态码
     * @return 状态描述
     */
    public static String getDescriptionByCode(Integer code) {
        CheckInStatusEnum status = getByCode(code);
        return status != null ? status.getDescription() : null;
    }

    /**
     * 判断是否为申请中状态
     */
    public boolean isApplying() {
        return this == APPLYING;
    }

    /**
     * 判断是否为已入住状态
     */
    public boolean isCheckedIn() {
        return this == CHECKED_IN;
    }

    /**
     * 判断是否为已退宿状态
     */
    public boolean isCheckedOut() {
        return this == CHECKED_OUT;
    }

    /**
     * 判断是否为已拒绝状态
     */
    public boolean isRejected() {
        return this == REJECTED;
    }

    /**
     * 判断是否可以进行审批操作
     */
    public boolean canApprove() {
        return this == APPLYING;
    }

    /**
     * 判断是否可以进行分配宿舍操作
     */
    public boolean canAssignDormitory() {
        return this == CHECKED_IN;
    }

    /**
     * 判断是否可以进行退宿操作
     */
    public boolean canCheckout() {
        return this == CHECKED_IN;
    }

    /**
     * 判断是否可以取消申请
     */
    public boolean canCancel() {
        return this == APPLYING;
    }
}