package com.dormitory.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.InspectionDTO;
import com.dormitory.management.dto.InspectionPageDTO;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.service.InspectionService;
import com.dormitory.management.vo.DormitoryVO;
import com.dormitory.management.vo.InspectionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 卫生检查控制器
 */
@Slf4j
@RestController
@RequestMapping("/inspection")
@RequiredArgsConstructor
@Tag(name = "卫生检查管理", description = "卫生检查管理相关接口")
public class InspectionController {

    private final InspectionService inspectionService;
    private final DormitoryService dormitoryService;

    @Operation(summary = "分页查询卫生检查记录")
    @PostMapping("/page")
    public Result<IPage<InspectionVO>> page(@RequestBody InspectionPageDTO params) {
        try {
            IPage<InspectionVO> result = inspectionService.getInspectionPage(params);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询卫生检查记录失败", e);
            return Result.error("查询卫生检查记录失败");
        }
    }

    @Operation(summary = "根据ID查询检查详情")
    @GetMapping("/{id}")
    public Result<InspectionVO> getById(@PathVariable Long id) {
        try {
            InspectionVO inspection = inspectionService.getInspectionById(id);
            if (inspection == null) {
                return Result.error("检查记录不存在");
            }
            return Result.success(inspection);
        } catch (Exception e) {
            log.error("查询检查详情失败", e);
            return Result.error("查询检查详情失败");
        }
    }

    @Operation(summary = "新增卫生检查记录")
    @PostMapping
    public Result<Void> add(@RequestBody @Validated InspectionDTO inspectionDTO) {
        try {
            boolean result = inspectionService.addInspection(inspectionDTO, "admin");
            if (result) {
                return Result.success();
            }
            return Result.error("新增检查记录失败");
        } catch (Exception e) {
            log.error("新增检查记录失败", e);
            return Result.error("新增检查记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新卫生检查记录")
    @PutMapping
    public Result<Void> update(@RequestBody @Validated InspectionDTO inspectionDTO) {
        try {
            boolean result = inspectionService.updateInspection(inspectionDTO, "admin");
            if (result) {
                return Result.success();
            }
            return Result.error("更新检查记录失败");
        } catch (Exception e) {
            log.error("更新检查记录失败", e);
            return Result.error("更新检查记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除卫生检查记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean result = inspectionService.deleteInspection(id);
            if (result) {
                return Result.success();
            }
            return Result.error("删除检查记录失败");
        } catch (Exception e) {
            log.error("删除检查记录失败", e);
            return Result.error("删除检查记录失败");
        }
    }

    @Operation(summary = "批量删除检查记录")
    @DeleteMapping("/batch")
    public Result<Map<String, Object>> batchDelete(@RequestBody List<Long> ids) {
        try {
            Map<String, Object> result = inspectionService.batchDeleteInspection(ids);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量删除检查记录失败", e);
            return Result.error("批量删除检查记录失败");
        }
    }

    @Operation(summary = "获取宿舍选项列表")
    @GetMapping("/dormitories/options")
    public Result<List<DormitoryVO>> getDormitoryOptions() {
        try {
            List<DormitoryVO> dormitories = dormitoryService.getAllDormitories();
            return Result.success(dormitories);
        } catch (Exception e) {
            log.error("获取宿舍选项失败", e);
            return Result.error("获取宿舍选项失败");
        }
    }

    @Operation(summary = "获取检查统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> statistics = inspectionService.getInspectionStatistics(startDate, endDate);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取检查统计信息失败", e);
            return Result.error("获取检查统计信息失败");
        }
    }

    @Operation(summary = "获取不合格宿舍列表")
    @GetMapping("/failed")
    public Result<List<InspectionVO>> getFailedInspections(
            @RequestParam(defaultValue = "30") Integer days) {
        try {
            List<InspectionVO> failedInspections = inspectionService.getFailedInspections(days);
            return Result.success(failedInspections);
        } catch (Exception e) {
            log.error("获取不合格宿舍列表失败", e);
            return Result.error("获取不合格宿舍列表失败");
        }
    }

    @Operation(summary = "获取宿舍检查历史")
    @GetMapping("/history/{roomId}")
    public Result<List<InspectionVO>> getInspectionHistory(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<InspectionVO> history = inspectionService.getInspectionHistoryByRoomId(roomId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取宿舍检查历史失败", e);
            return Result.error("获取宿舍检查历史失败");
        }
    }
}