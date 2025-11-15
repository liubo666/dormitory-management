package com.dormitory.management.service;

import com.dormitory.management.vo.StatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取综合统计信息
     * @return 统计信息
     */
    StatisticsVO getOverallStatistics();

    /**
     * 获取学生统计信息
     * @return 学生统计信息
     */
    StatisticsVO.StudentStatistics getStudentStatistics();

    /**
     * 获取宿舍统计信息
     * @return 宿舍统计信息
     */
    StatisticsVO.DormitoryStatistics getDormitoryStatistics();

    /**
     * 获取费用统计信息
     * @return 费用统计信息
     */
    StatisticsVO.FeeStatistics getFeeStatistics();

    /**
     * 获取访客统计信息
     * @return 访客统计信息
     */
    StatisticsVO.VisitorStatistics getVisitorStatistics();
}