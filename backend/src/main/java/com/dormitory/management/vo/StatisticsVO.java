package com.dormitory.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据统计视图对象
 */
@Data
public class StatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学生统计
     */
    private StudentStatistics studentStatistics;

    /**
     * 宿舍统计
     */
    private DormitoryStatistics dormitoryStatistics;

    /**
     * 费用统计
     */
    private FeeStatistics feeStatistics;

    /**
     * 访客统计
     */
    private VisitorStatistics visitorStatistics;

    /**
     * 统计时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statisticsTime;

    /**
     * 学生统计信息
     */
    @Data
    public static class StudentStatistics implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 总学生数
         */
        private Integer totalStudents;

        /**
         * 已入住学生数
         */
        private Integer checkedInStudents;

        /**
         * 待分配学生数
         */
        private Integer pendingStudents;

        /**
         * 申请中学生数
         */
        private Integer applyingStudents;

        /**
         * 已退宿学生数
         */
        private Integer checkedOutStudents;

        /**
         * 已拒绝学生数
         */
        private Integer rejectedStudents;

        /**
         * 休学学生数
         */
        private Integer suspendedStudents;

        /**
         * 毕业学生数
         */
        private Integer graduatedStudents;

        /**
         * 入住率(%)
         */
        private Double checkInRate;

        /**
         * 各年级分布
         */
        private GradeDistribution gradeDistribution;
    }

    /**
     * 年级分布
     */
    @Data
    public static class GradeDistribution implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 2021级人数
         */
        private Integer grade2021;

        /**
         * 2022级人数
         */
        private Integer grade2022;

        /**
         * 2023级人数
         */
        private Integer grade2023;

        /**
         * 2024级人数
         */
        private Integer grade2024;

        /**
         * 其他年级人数
         */
        private Integer otherGrades;
    }

    /**
     * 宿舍统计信息
     */
    @Data
    public static class DormitoryStatistics implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 总房间数
         */
        private Integer totalRooms;

        /**
         * 已入住房间数
         */
        private Integer occupiedRooms;

        /**
         * 空房间数
         */
        private Integer availableRooms;

        /**
         * 入住率(%)
         */
        private Double occupancyRate;

        /**
         * 总床位数
         */
        private Integer totalBeds;

        /**
         * 已占用床位数
         */
        private Integer occupiedBeds;

        /**
         * 空床位数
         */
        private Integer availableBeds;

        /**
         * 床位入住率(%)
         */
        private Double bedOccupancyRate;

        /**
         * 各楼栋分布
         */
        private BuildingDistribution buildingDistribution;
    }

    /**
     * 楼栋分布
     */
    @Data
    public static class BuildingDistribution implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 1号楼房间数
         */
        private Integer building1Rooms;

        /**
         * 2号楼房间数
         */
        private Integer building2Rooms;

        /**
         * 3号楼房间数
         */
        private Integer building3Rooms;

        /**
         * 4号楼房间数
         */
        private Integer building4Rooms;

        /**
         * 其他楼栋房间数
         */
        private Integer otherBuildingsRooms;

        /**
         * 1号楼入住率
         */
        private Double building1OccupancyRate;

        /**
         * 2号楼入住率
         */
        private Double building2OccupancyRate;

        /**
         * 3号楼入住率
         */
        private Double building3OccupancyRate;

        /**
         * 4号楼入住率
         */
        private Double building4OccupancyRate;

        /**
         * 其他楼栋入住率
         */
        private Double otherBuildingsOccupancyRate;
    }

    /**
     * 费用统计信息
     */
    @Data
    public static class FeeStatistics implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 总费用数
         */
        private Integer totalFees;

        /**
         * 总费用金额
         */
        private java.math.BigDecimal totalAmount;

        /**
         * 已收费用金额
         */
        private java.math.BigDecimal paidAmount;

        /**
         * 未收费用金额
         */
        private java.math.BigDecimal unpaidAmount;

        /**
         * 收费率(%)
         */
        private Double collectionRate;

        /**
         * 未支付费用数
         */
        private Integer unpaidCount;

        /**
         * 部分支付费用数
         */
        private Integer partiallyPaidCount;

        /**
         * 已支付费用数
         */
        private Integer fullyPaidCount;

        /**
         * 各费用类型分布
         */
        private FeeTypeDistribution feeTypeDistribution;

        /**
         * 本月收费金额
         */
        private java.math.BigDecimal monthlyAmount;

        /**
         * 本年收费金额
         */
        private java.math.BigDecimal yearlyAmount;
    }

    /**
     * 费用类型分布
     */
    @Data
    public static class FeeTypeDistribution implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 住宿费金额
         */
        private java.math.BigDecimal accommodationAmount;

        /**
         * 水电费金额
         */
        private java.math.BigDecimal utilitiesAmount;

        /**
         * 网费金额
         */
        private java.math.BigDecimal internetAmount;

        /**
         * 其他费用金额
         */
        private java.math.BigDecimal otherAmount;
    }

    /**
     * 访客统计信息
     */
    @Data
    public static class VisitorStatistics implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 总访客数
         */
        private Integer totalVisitors;

        /**
         * 今日访客数
         */
        private Integer todayVisitors;

        /**
         * 本周访客数
         */
        private Integer weeklyVisitors;

        /**
         * 本月访客数
         */
        private Integer monthlyVisitors;

        /**
         * 待访问访客数
         */
        private Integer pendingVisitors;

        /**
         * 访问中访客数
         */
        private Integer visitingVisitors;

        /**
         * 已完成访客数
         */
        private Integer completedVisitors;

        /**
         * 已取消访客数
         */
        private Integer cancelledVisitors;

        /**
         * 各访问状态分布
         */
        private VisitorStatusDistribution statusDistribution;

        /**
         * 今日访客趋势
         */
        private DailyVisitorTrend dailyTrend;
    }

    /**
     * 访客状态分布
     */
    @Data
    public static class VisitorStatusDistribution implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 待访问数量
         */
        private Integer pendingCount;

        /**
         * 访问中数量
         */
        private Integer visitingCount;

        /**
         * 已完成数量
         */
        private Integer completedCount;

        /**
         * 已取消数量
         */
        private Integer cancelledCount;
    }

    /**
     * 每日访客趋势
     */
    @Data
    public static class DailyVisitorTrend implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 最近7天访客数据
         */
        private java.util.List<DailyVisitorCount> dailyCounts;
    }

    /**
     * 每日访客数
     */
    @Data
    public static class DailyVisitorCount implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 日期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private String date;

        /**
         * 访客数
         */
        private Integer count;
    }
}