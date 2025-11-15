package com.dormitory.management.enums;

/**
 * 报修状态枚举
 */
public enum RepairStatusEnum {

    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String description;

    RepairStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(Integer code) {
        for (RepairStatusEnum status : RepairStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知";
    }
}