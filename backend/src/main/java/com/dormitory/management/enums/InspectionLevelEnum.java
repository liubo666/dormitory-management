package com.dormitory.management.enums;

import lombok.Getter;

/**
 * 卫生检查等级枚举
 */
@Getter
public enum InspectionLevelEnum {

    EXCELLENT("excellent", "优秀", 90, 100),
    GOOD("good", "良好", 80, 89),
    PASS("pass", "合格", 60, 79),
    FAIL("fail", "不合格", 0, 59);

    private final String code;
    private final String description;
    private final Integer minScore;
    private final Integer maxScore;

    InspectionLevelEnum(String code, String description, Integer minScore, Integer maxScore) {
        this.code = code;
        this.description = description;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    /**
     * 根据分数获取等级
     */
    public static String getLevelByScore(Integer score) {
        if (score >= EXCELLENT.getMinScore()) {
            return EXCELLENT.getCode();
        } else if (score >= GOOD.getMinScore()) {
            return GOOD.getCode();
        } else if (score >= PASS.getMinScore()) {
            return PASS.getCode();
        } else {
            return FAIL.getCode();
        }
    }

    /**
     * 根据等级代码获取描述
     */
    public static String getDescriptionByCode(String code) {
        for (InspectionLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level.getDescription();
            }
        }
        return "未知";
    }
}