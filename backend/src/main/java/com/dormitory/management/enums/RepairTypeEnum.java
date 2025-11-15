package com.dormitory.management.enums;

/**
 * 报修类型枚举
 */
public enum RepairTypeEnum {

    WATER("water", "水电维修"),
    ELECTRIC("electric", "电路维修"),
    DOOR("door", "门窗维修"),
    FURNITURE("furniture", "家具维修"),
    OTHER("other", "其他");

    private final String code;
    private final String description;

    RepairTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(String code) {
        for (RepairTypeEnum type : RepairTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type.getDescription();
            }
        }
        return "未知";
    }
}