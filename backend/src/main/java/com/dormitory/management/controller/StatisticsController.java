package com.dormitory.management.controller;

import com.dormitory.management.common.Result;
import com.dormitory.management.service.StatisticsService;
import com.dormitory.management.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计信息控制器
 */
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "统计信息管理", description = "统计信息相关接口")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取综合统计信息
     */
    @GetMapping("/overall")
    @Operation(summary = "获取综合统计信息", description = "获取包含学生、宿舍、费用、访客的综合统计信息")
    public Result<StatisticsVO> getOverallStatistics() {
        StatisticsVO statistics = statisticsService.getOverallStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取学生统计信息
     */
    @GetMapping("/student")
    @Operation(summary = "获取学生统计信息", description = "获取学生相关的统计信息，包括入住率、年级分布等")
    public Result<StatisticsVO.StudentStatistics> getStudentStatistics() {
        StatisticsVO.StudentStatistics studentStatistics = statisticsService.getStudentStatistics();
        return Result.success(studentStatistics);
    }

    /**
     * 获取宿舍统计信息
     */
    @GetMapping("/dormitory")
    @Operation(summary = "获取宿舍统计信息", description = "获取宿舍相关的统计信息，包括房间使用率、楼栋分布等")
    public Result<StatisticsVO.DormitoryStatistics> getDormitoryStatistics() {
        StatisticsVO.DormitoryStatistics dormitoryStatistics = statisticsService.getDormitoryStatistics();
        return Result.success(dormitoryStatistics);
    }

    /**
     * 获取费用统计信息
     */
    @GetMapping("/fee")
    @Operation(summary = "获取费用统计信息", description = "获取费用相关的统计信息，包括收费率、费用类型分布等")
    public Result<StatisticsVO.FeeStatistics> getFeeStatistics() {
        StatisticsVO.FeeStatistics feeStatistics = statisticsService.getFeeStatistics();
        return Result.success(feeStatistics);
    }

    /**
     * 获取访客统计信息
     */
    @GetMapping("/visitor")
    @Operation(summary = "获取访客统计信息", description = "获取访客相关的统计信息，包括访问状态分布、每日趋势等")
    public Result<StatisticsVO.VisitorStatistics> getVisitorStatistics() {
        StatisticsVO.VisitorStatistics visitorStatistics = statisticsService.getVisitorStatistics();
        return Result.success(visitorStatistics);
    }
}