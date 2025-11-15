package com.dormitory.management.enums;

/**
 * 访客状态枚举
 */
public enum VisitorStatusEnum {
    PENDING(0, "待访问"),
    IN_PROGRESS(1, "访问中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");

    private final Integer code;
    private final String description;

    VisitorStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        for (VisitorStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知状态";
    }

    /**
     * 根据代码获取枚举
     */
    public static VisitorStatusEnum getByCode(Integer code) {
        for (VisitorStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}