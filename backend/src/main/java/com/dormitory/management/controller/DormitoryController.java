package com.dormitory.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.DormitoryDTO;
import com.dormitory.management.dto.DormitoryPageDTO;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.vo.DormitoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宿舍管理控制器
 */
@Tag(name = "宿舍管理", description = "宿舍相关接口")
@RestController
@RequestMapping("/dormitory")
@RequiredArgsConstructor
public class DormitoryController {

    private final DormitoryService dormitoryService;

    @Operation(summary = "分页查询宿舍列表")
    @PostMapping("/page")
    public Result<Page<DormitoryVO>> getDormitoryPage(@RequestBody DormitoryPageDTO pageDTO) {
        Page<DormitoryVO> result = dormitoryService.getDormitoryPage(
                pageDTO
        );
        return Result.success(result);
    }

    @Operation(summary = "根据ID获取宿舍详情")
    @GetMapping("/{id}")
    public Result<DormitoryVO> getDormitoryById(
            @Parameter(description = "宿舍ID") @PathVariable Long id) {

        DormitoryVO dormitory = dormitoryService.getDormitoryById(id);
        if (dormitory == null) {
            return Result.error("宿舍不存在");
        }
        return Result.success(dormitory);
    }

    @Operation(summary = "获取可用宿舍列表")
    @GetMapping("/available")
    public Result<List<DormitoryVO>> getAvailableDormitories(
            @Parameter(description = "楼栋ID") @RequestParam(required = false) Long buildingId) {

        List<DormitoryVO> dormitories = dormitoryService.getAvailableDormitories(buildingId);
        return Result.success(dormitories);
    }

    @Operation(summary = "新增宿舍")
    @PostMapping
    public Result<Void> createDormitory(
            @Validated @RequestBody DormitoryDTO dormitoryDTO) {

        try {
            boolean success = dormitoryService.createDormitory(dormitoryDTO);
            if (success) {
                return Result.success();
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

@Operation(summary = "更新宿舍")
    @PutMapping("/{id}")
    public Result<Void> updateDormitory(
            @Parameter(description = "宿舍ID") @PathVariable String id,
            @Validated @RequestBody DormitoryDTO dormitoryDTO) {

        try {
            dormitoryDTO.setId(Long.parseLong(id));
            boolean success = dormitoryService.createDormitory(dormitoryDTO);
            if (success) {
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除宿舍")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDormitory(
            @Parameter(description = "宿舍ID") @PathVariable Long id) {

        try {
            boolean success = dormitoryService.deleteDormitory(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

//    @Operation(summary = "修改宿舍状态")
//    @GetMapping("/status")
//    public Result<Void> updateDormitoryStatus(
//            @Parameter(description = "宿舍ID") @RequestParam String id,
//            @Parameter(description = "状态") @RequestParam Integer status) {
//
//        try {
//            // 这里可以从SecurityContext获取当前用户
//            String currentUser = "admin"; // 实际应用中应该从Spring Security获取
//            boolean success = dormitoryService.updateDormitoryStatus(id, status, currentUser);
//            if (success) {
//                return Result.success();
//            } else {
//                return Result.error("状态更新失败");
//            }
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    @Operation(summary = "获取宿舍床位信息")
//    @GetMapping("/{id}/beds")
//    public Result<Map<String, Object>> getDormitoryBeds(
//            @Parameter(description = "宿舍ID") @PathVariable String id) {
//
//        try {
//            Map<String, Object> bedInfo = dormitoryService.getDormitoryBeds(id);
//
//            // 构造床位列表（模拟数据，实际应该从数据库查询）
//            Integer bedCount = (Integer) bedInfo.get("bedCount");
//            Integer occupiedBeds = (Integer) bedInfo.get("occupiedBeds");
//
//            // 构造床位详情列表
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> bedList = (List<Map<String, Object>>) bedInfo.get("bedList");
//            if (bedList == null) {
//                bedList = java.util.Arrays.asList(
//                    Map.of("bedNo", "床位1", "isOccupied", false, "studentName", "", "studentNo", ""),
//                    Map.of("bedNo", "床位2", "isOccupied", false, "studentName", "", "studentNo", ""),
//                    Map.of("bedNo", "床位3", "isOccupied", false, "studentName", "", "studentNo", ""),
//                    Map.of("bedNo", "床位4", "isOccupied", false, "studentName", "", "studentNo", "")
//                );
//
//                // 模拟已入住床位
//                for (int i = 0; i < occupiedBeds && i < bedList.size(); i++) {
//                    Map<String, Object> bed = bedList.get(i);
//                    bed.put("isOccupied", true);
//                    bed.put("studentName", "学生" + (i + 1));
//                    bed.put("studentNo", "202400" + (i + 1));
//                }
//            }
//
//            Map<String, Object> result = new java.util.HashMap<>();
//            result.put("bedCount", bedCount);
//            result.put("occupiedBeds", occupiedBeds);
//            result.put("availableBeds", bedCount - occupiedBeds);
//            result.put("bedList", bedList);
//
//            return Result.success(result);
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
}