package com.dormitory.management.enums;

/**
 * 报修优先级枚举
 */
public enum RepairPriorityEnum {

    LOW(1, "低"),
    MEDIUM(2, "中"),
    HIGH(3, "高");

    private final Integer code;
    private final String description;

    RepairPriorityEnum(Integer code, String description) {
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
        for (RepairPriorityEnum priority : RepairPriorityEnum.values()) {
            if (priority.getCode().equals(code)) {
                return priority.getDescription();
            }
        }
        return "未知";
    }
}