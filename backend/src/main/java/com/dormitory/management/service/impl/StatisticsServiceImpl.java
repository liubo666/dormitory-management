package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.management.entity.CheckIn;
import com.dormitory.management.entity.Fee;
import com.dormitory.management.entity.Student;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.Visitor;
import com.dormitory.management.mapper.CheckInMapper;
import com.dormitory.management.mapper.FeeMapper;
import com.dormitory.management.mapper.StudentMapper;
import com.dormitory.management.mapper.DormitoryMapper;
import com.dormitory.management.mapper.VisitorMapper;
import com.dormitory.management.service.StatisticsService;
import com.dormitory.management.vo.StatisticsVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StudentMapper studentMapper;
    private final DormitoryMapper dormitoryMapper;
    private final CheckInMapper checkInMapper;
    private final FeeMapper feeMapper;
    private final VisitorMapper visitorMapper;

    public StatisticsServiceImpl(StudentMapper studentMapper, DormitoryMapper dormitoryMapper,
                               CheckInMapper checkInMapper, FeeMapper feeMapper, VisitorMapper visitorMapper) {
        this.studentMapper = studentMapper;
        this.dormitoryMapper = dormitoryMapper;
        this.checkInMapper = checkInMapper;
        this.feeMapper = feeMapper;
        this.visitorMapper = visitorMapper;
    }

    @Override
    public StatisticsVO getOverallStatistics() {
        StatisticsVO statisticsVO = new StatisticsVO();
        statisticsVO.setStudentStatistics(getStudentStatistics());
        statisticsVO.setDormitoryStatistics(getDormitoryStatistics());
        statisticsVO.setFeeStatistics(getFeeStatistics());
        statisticsVO.setVisitorStatistics(getVisitorStatistics());
        statisticsVO.setStatisticsTime(LocalDateTime.now());
        return statisticsVO;
    }

    @Override
    public StatisticsVO.StudentStatistics getStudentStatistics() {
        StatisticsVO.StudentStatistics studentStats = new StatisticsVO.StudentStatistics();

        // 查询所有学生
        Long totalStudentsCount = studentMapper.selectCount(new LambdaQueryWrapper<>());
        studentStats.setTotalStudents(totalStudentsCount.intValue());

        // 查询已入住学生（状态为2的入住记录）
        Long checkedInStudentsCount = checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 2));
        studentStats.setCheckedInStudents(checkedInStudentsCount.intValue());

        // 查询申请中学生（状态为1的入住记录）
        Long applyingStudentsCount = checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 1));
        studentStats.setApplyingStudents(applyingStudentsCount.intValue());

        // 查询已退宿学生（状态为3的入住记录）
        Long checkedOutStudentsCount = checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 3));
        studentStats.setCheckedOutStudents(checkedOutStudentsCount.intValue());

        // 查询已拒绝学生（状态为4的入住记录）
        Long rejectedStudentsCount = checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 4));
        studentStats.setRejectedStudents(rejectedStudentsCount.intValue());

        // 计算待分配学生数（没有入住记录的学生）
        Integer pendingStudents = totalStudentsCount.intValue() - checkedInStudentsCount.intValue() - applyingStudentsCount.intValue() - checkedOutStudentsCount.intValue() - rejectedStudentsCount.intValue();
        studentStats.setPendingStudents(Math.max(0, pendingStudents));

        // 设置默认值
        studentStats.setSuspendedStudents(0);
        studentStats.setGraduatedStudents(0);

        // 计算入住率
        if (totalStudentsCount.intValue() > 0) {
            double checkInRate = (double) checkedInStudentsCount.intValue() / totalStudentsCount.intValue() * 100;
            studentStats.setCheckInRate(Math.round(checkInRate * 100.0) / 100.0);
        } else {
            studentStats.setCheckInRate(0.0);
        }

        // 设置年级分布（根据学生年级字段查询）
        StatisticsVO.GradeDistribution gradeDistribution = new StatisticsVO.GradeDistribution();

        // 查询各年级学生数
        Long grade2021Count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .like(Student::getGrade, "2021"));
        Long grade2022Count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .like(Student::getGrade, "2022"));
        Long grade2023Count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .like(Student::getGrade, "2023"));
        Long grade2024Count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .like(Student::getGrade, "2024"));

        gradeDistribution.setGrade2021(grade2021Count.intValue());
        gradeDistribution.setGrade2022(grade2022Count.intValue());
        gradeDistribution.setGrade2023(grade2023Count.intValue());
        gradeDistribution.setGrade2024(grade2024Count.intValue());
        gradeDistribution.setOtherGrades(totalStudentsCount.intValue() - grade2021Count.intValue() - grade2022Count.intValue() - grade2023Count.intValue() - grade2024Count.intValue());
        studentStats.setGradeDistribution(gradeDistribution);

        return studentStats;
    }

    @Override
    public StatisticsVO.DormitoryStatistics getDormitoryStatistics() {
        StatisticsVO.DormitoryStatistics dormitoryStats = new StatisticsVO.DormitoryStatistics();

        // 查询总房间数和总床位数（模拟数据，实际需要根据dormitory表查询）
        Long totalRoomsCount = dormitoryMapper.selectCount(new LambdaQueryWrapper<>());
        dormitoryStats.setTotalRooms(totalRoomsCount.intValue());

        // 假设每个房间4个床位
        Integer totalBeds = totalRoomsCount.intValue() * 4;
        dormitoryStats.setTotalBeds(totalBeds);

        // 查询已入住学生数
        Long occupiedBedsCount = checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 2));
        dormitoryStats.setOccupiedBeds(occupiedBedsCount.intValue());

        // 简化计算：假设每个学生占用一个房间
        // 实际应该根据不同的dormitoryId来计算，但为了简化，这里使用保守估计
        Long occupiedRoomsCount = Math.min(occupiedBedsCount, totalRoomsCount);
        dormitoryStats.setOccupiedRooms(occupiedRoomsCount.intValue());

        // 计算空房间数和空床位数
        dormitoryStats.setAvailableRooms(Math.max(0, totalRoomsCount.intValue() - occupiedRoomsCount.intValue()));
        dormitoryStats.setAvailableBeds(Math.max(0, totalBeds - occupiedBedsCount.intValue()));

        // 计算入住率（基于房间数）
        if (totalRoomsCount.intValue() > 0) {
            double occupancyRate = (double) occupiedRoomsCount.intValue() / totalRoomsCount.intValue() * 100;
            dormitoryStats.setOccupancyRate(Math.round(Math.min(100, occupancyRate) * 100.0) / 100.0);
        } else {
            dormitoryStats.setOccupancyRate(0.0);
        }

        // 计算床位入住率
        if (totalBeds > 0) {
            double bedOccupancyRate = (double) occupiedBedsCount.intValue() / totalBeds * 100;
            dormitoryStats.setBedOccupancyRate(Math.round(bedOccupancyRate * 100.0) / 100.0);
        } else {
            dormitoryStats.setBedOccupancyRate(0.0);
        }

        // 设置楼栋分布（根据楼栋字段查询）
        StatisticsVO.BuildingDistribution buildingDistribution = new StatisticsVO.BuildingDistribution();

        // 查询各楼栋房间数
        Long building1RoomsCount = dormitoryMapper.selectCount(new LambdaQueryWrapper<Dormitory>()
                .like(Dormitory::getBuildingName, "1号楼"));
        Long building2RoomsCount = dormitoryMapper.selectCount(new LambdaQueryWrapper<Dormitory>()
                .like(Dormitory::getBuildingName, "2号楼"));
        Long building3RoomsCount = dormitoryMapper.selectCount(new LambdaQueryWrapper<Dormitory>()
                .like(Dormitory::getBuildingName, "3号楼"));
        Long building4RoomsCount = dormitoryMapper.selectCount(new LambdaQueryWrapper<Dormitory>()
                .like(Dormitory::getBuildingName, "4号楼"));

        buildingDistribution.setBuilding1Rooms(building1RoomsCount.intValue());
        buildingDistribution.setBuilding2Rooms(building2RoomsCount.intValue());
        buildingDistribution.setBuilding3Rooms(building3RoomsCount.intValue());
        buildingDistribution.setBuilding4Rooms(building4RoomsCount.intValue());
        buildingDistribution.setOtherBuildingsRooms(totalRoomsCount.intValue() - building1RoomsCount.intValue() - building2RoomsCount.intValue() - building3RoomsCount.intValue() - building4RoomsCount.intValue());

        // 计算各楼栋入住率
        double building1OccupancyRate = building1RoomsCount.intValue() > 0 ?
            (double) checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 2)
                .like(CheckIn::getBuildingName, "1号楼")) / building1RoomsCount.intValue() * 100 : 0.0;
        double building2OccupancyRate = building2RoomsCount.intValue() > 0 ?
            (double) checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 2)
                .like(CheckIn::getBuildingName, "2号楼")) / building2RoomsCount.intValue() * 100 : 0.0;
        double building3OccupancyRate = building3RoomsCount.intValue() > 0 ?
            (double) checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 2)
                .like(CheckIn::getBuildingName, "3号楼")) / building3RoomsCount.intValue() * 100 : 0.0;
        double building4OccupancyRate = building4RoomsCount.intValue() > 0 ?
            (double) checkInMapper.selectCount(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getStatus, 2)
                .like(CheckIn::getBuildingName, "4号楼")) / building4RoomsCount.intValue() * 100 : 0.0;

        buildingDistribution.setBuilding1OccupancyRate(Math.round(building1OccupancyRate * 100.0) / 100.0);
        buildingDistribution.setBuilding2OccupancyRate(Math.round(building2OccupancyRate * 100.0) / 100.0);
        buildingDistribution.setBuilding3OccupancyRate(Math.round(building3OccupancyRate * 100.0) / 100.0);
        buildingDistribution.setBuilding4OccupancyRate(Math.round(building4OccupancyRate * 100.0) / 100.0);
        buildingDistribution.setOtherBuildingsOccupancyRate(0.0);
        dormitoryStats.setBuildingDistribution(buildingDistribution);

        return dormitoryStats;
    }

    @Override
    public StatisticsVO.FeeStatistics getFeeStatistics() {
        StatisticsVO.FeeStatistics feeStats = new StatisticsVO.FeeStatistics();

        // 查询所有费用
        List<Fee> fees = feeMapper.selectList(new LambdaQueryWrapper<>());
        feeStats.setTotalFees(fees.size());

        // 统计费用金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        int unpaidCount = 0;
        int partiallyPaidCount = 0;
        int fullyPaidCount = 0;

        BigDecimal accommodationAmount = BigDecimal.ZERO;
        BigDecimal utilitiesAmount = BigDecimal.ZERO;
        BigDecimal internetAmount = BigDecimal.ZERO;
        BigDecimal otherAmount = BigDecimal.ZERO;

        for (Fee fee : fees) {
            BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
            BigDecimal paid = fee.getPaidAmount() != null ? fee.getPaidAmount() : BigDecimal.ZERO;

            totalAmount = totalAmount.add(amount);
            paidAmount = paidAmount.add(paid);

            // 统计支付状态
            Integer status = fee.getPaymentStatus();
            if (status != null) {
                switch (status) {
                    case 0:
                        unpaidCount++;
                        break;
                    case 1:
                        partiallyPaidCount++;
                        break;
                    case 2:
                        fullyPaidCount++;
                        break;
                }
            }

            // 统计费用类型分布
            Integer feeType = fee.getFeeType();
            if (feeType != null) {
                switch (feeType) {
                    case 1:
                        accommodationAmount = accommodationAmount.add(amount);
                        break;
                    case 2:
                        utilitiesAmount = utilitiesAmount.add(amount);
                        break;
                    case 3:
                        internetAmount = internetAmount.add(amount);
                        break;
                    case 4:
                        otherAmount = otherAmount.add(amount);
                        break;
                }
            }
        }

        BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

        feeStats.setTotalAmount(totalAmount);
        feeStats.setPaidAmount(paidAmount);
        feeStats.setUnpaidAmount(unpaidAmount);
        feeStats.setUnpaidCount(unpaidCount);
        feeStats.setPartiallyPaidCount(partiallyPaidCount);
        feeStats.setFullyPaidCount(fullyPaidCount);

        // 计算收费率
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            double collectionRate = paidAmount.doubleValue() / totalAmount.doubleValue() * 100;
            feeStats.setCollectionRate(Math.round(collectionRate * 100.0) / 100.0);
        } else {
            feeStats.setCollectionRate(0.0);
        }

        // 设置费用类型分布
        StatisticsVO.FeeTypeDistribution feeTypeDistribution = new StatisticsVO.FeeTypeDistribution();
        feeTypeDistribution.setAccommodationAmount(accommodationAmount);
        feeTypeDistribution.setUtilitiesAmount(utilitiesAmount);
        feeTypeDistribution.setInternetAmount(internetAmount);
        feeTypeDistribution.setOtherAmount(otherAmount);
        feeStats.setFeeTypeDistribution(feeTypeDistribution);

        // 计算本月和本年收费金额（简化计算）
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime yearStart = LocalDateTime.of(LocalDate.now().withDayOfYear(1), LocalTime.MIN);

        BigDecimal monthlyAmount = BigDecimal.ZERO;
        BigDecimal yearlyAmount = BigDecimal.ZERO;

        List<Fee> monthlyFees = feeMapper.selectList(new LambdaQueryWrapper<Fee>()
                .ge(Fee::getCreateTime, monthStart));

        List<Fee> yearlyFees = feeMapper.selectList(new LambdaQueryWrapper<Fee>()
                .ge(Fee::getCreateTime, yearStart));

        for (Fee fee : monthlyFees) {
            if (fee.getPaymentStatus() != null && fee.getPaymentStatus() == 2) {
                monthlyAmount = monthlyAmount.add(fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO);
            }
        }

        for (Fee fee : yearlyFees) {
            if (fee.getPaymentStatus() != null && fee.getPaymentStatus() == 2) {
                yearlyAmount = yearlyAmount.add(fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO);
            }
        }

        feeStats.setMonthlyAmount(monthlyAmount);
        feeStats.setYearlyAmount(yearlyAmount);

        return feeStats;
    }

    @Override
    public StatisticsVO.VisitorStatistics getVisitorStatistics() {
        StatisticsVO.VisitorStatistics visitorStats = new StatisticsVO.VisitorStatistics();

        // 查询所有访客
        List<Visitor> visitors = visitorMapper.selectList(new LambdaQueryWrapper<>());
        visitorStats.setTotalVisitors(visitors.size());

        // 查询今日访客
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<Visitor> todayVisitors = visitorMapper.selectList(new LambdaQueryWrapper<Visitor>()
                .between(Visitor::getCreateTime, todayStart, todayEnd));
        visitorStats.setTodayVisitors(todayVisitors.size());

        // 查询本周访客（简化计算）
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        List<Visitor> weeklyVisitors = visitorMapper.selectList(new LambdaQueryWrapper<Visitor>()
                .ge(Visitor::getCreateTime, weekStart));
        visitorStats.setWeeklyVisitors(weeklyVisitors.size());

        // 查询本月访客
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        List<Visitor> monthlyVisitors = visitorMapper.selectList(new LambdaQueryWrapper<Visitor>()
                .ge(Visitor::getCreateTime, monthStart));
        visitorStats.setMonthlyVisitors(monthlyVisitors.size());

        // 统计访客状态（根据Visitor实体的status字段查询）
        Long pendingVisitorsCount = visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                .eq(Visitor::getStatus, 1)); // 1:待访问
        Long visitingVisitorsCount = visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                .eq(Visitor::getStatus, 2)); // 2:访问中
        Long completedVisitorsCount = visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                .eq(Visitor::getStatus, 3)); // 3:已完成
        Long cancelledVisitorsCount = visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                .eq(Visitor::getStatus, 4)); // 4:已取消

        int pendingVisitors = pendingVisitorsCount.intValue();
        int visitingVisitors = visitingVisitorsCount.intValue();
        int completedVisitors = completedVisitorsCount.intValue();
        int cancelledVisitors = cancelledVisitorsCount.intValue();

        visitorStats.setPendingVisitors(pendingVisitors);
        visitorStats.setVisitingVisitors(visitingVisitors);
        visitorStats.setCompletedVisitors(completedVisitors);
        visitorStats.setCancelledVisitors(cancelledVisitors);

        // 设置状态分布
        StatisticsVO.VisitorStatusDistribution statusDistribution = new StatisticsVO.VisitorStatusDistribution();
        statusDistribution.setPendingCount(pendingVisitors);
        statusDistribution.setVisitingCount(visitingVisitors);
        statusDistribution.setCompletedCount(completedVisitors);
        statusDistribution.setCancelledCount(cancelledVisitors);
        visitorStats.setStatusDistribution(statusDistribution);

        // 设置每日访客趋势（最近7天）
        StatisticsVO.DailyVisitorTrend dailyTrend = new StatisticsVO.DailyVisitorTrend();
        List<StatisticsVO.DailyVisitorCount> dailyCounts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            List<Visitor> dayVisitors = visitorMapper.selectList(new LambdaQueryWrapper<Visitor>()
                    .between(Visitor::getCreateTime, dayStart, dayEnd));

            StatisticsVO.DailyVisitorCount dailyCount = new StatisticsVO.DailyVisitorCount();
            dailyCount.setDate(date.toString());
            dailyCount.setCount(dayVisitors.size());
            dailyCounts.add(dailyCount);
        }

        dailyTrend.setDailyCounts(dailyCounts);
        visitorStats.setDailyTrend(dailyTrend);

        return visitorStats;
    }
}