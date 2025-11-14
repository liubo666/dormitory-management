//package com.dormitory.management.controller;
//
//import com.dormitory.management.common.Result;
//import com.dormitory.management.dto.BedDTO;
//import com.dormitory.management.dto.DormitoryWithBedsDTO;
//import com.dormitory.management.service.BedService;
//import com.dormitory.management.service.SysUserService;
//import com.dormitory.management.context.UserContext;
//import com.dormitory.management.vo.BedVO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 床位管理控制器
// */
//@Tag(name = "床位管理", description = "床位相关接口")
//@RestController
//@RequestMapping("/bed")
//@RequiredArgsConstructor
//public class BedController {
//
//    private final BedService bedService;
//
//    /**
//     * 获取当前用户名
//     */
//    private String getCurrentUser() {
//        String username = UserContext.getCurrentUsername();
//        return username != null ? username : "system";
//    }
//
//    @Operation(summary = "根据宿舍ID获取床位列表")
//    @GetMapping("/dormitory/{dormitoryId}")
//    public Result<List<BedVO>> getBedsByDormitory(
//            @Parameter(description = "宿舍ID") @PathVariable String dormitoryId) {
//        List<BedVO> beds = bedService.getBedsByDormitoryId(dormitoryId);
//        return Result.success(beds);
//    }
//
//    @Operation(summary = "新增床位")
//    @PostMapping
//    public Result<Void> createBed(@Validated @RequestBody BedDTO bedDTO) {
//        try {
//            String currentUser = getCurrentUser();
//            boolean success = bedService.updateBed(bedDTO, currentUser);
//            if (success) {
//                return Result.success();
//            } else {
//                return Result.error("新增失败");
//            }
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    @Operation(summary = "更新床位信息")
//    @PutMapping("/{id}")
//    public Result<Void> updateBed(
//            @Parameter(description = "床位ID") @PathVariable String id,
//            @Validated @RequestBody BedDTO bedDTO) {
//        try {
//            bedDTO.setId(id);
//            String currentUser = getCurrentUser();
//            boolean success = bedService.updateBed(bedDTO, currentUser);
//            if (success) {
//                return Result.success();
//            } else {
//                return Result.error("更新失败");
//            }
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    @Operation(summary = "删除床位")
//    @DeleteMapping("/{id}")
//    public Result<Void> deleteBed(@Parameter(description = "床位ID") @PathVariable String id) {
//        try {
//            boolean success = bedService.deleteBed(id);
//            if (success) {
//                return Result.success();
//            } else {
//                return Result.error("删除失败");
//            }
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    @Operation(summary = "更新床位状态")
//    @PutMapping("/{id}/status")
//    public Result<Void> updateBedStatus(
//            @Parameter(description = "床位ID") @PathVariable String id,
//            @Parameter(description = "状态") @RequestParam Integer status) {
//        try {
//            String currentUser = getCurrentUser();
//            boolean success = bedService.updateBedStatus(id, status, currentUser);
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
//    @Operation(summary = "获取宿舍可用床位统计")
//    @GetMapping("/dormitory/{dormitoryId}/stats")
//    public Result<Object> getBedStats(@Parameter(description = "宿舍ID") @PathVariable Long dormitoryId) {
//        try {
//            Integer availableBeds = bedService.getAvailableBedCount(dormitoryId);
//            Integer occupiedBeds = bedService.getOccupiedBedCount(dormitoryId);
//            Integer totalBeds = availableBeds + occupiedBeds;
//
//            BedStats stats = new BedStats();
//            stats.setAvailableBeds(availableBeds);
//            stats.setOccupiedBeds(occupiedBeds);
//            stats.setTotalBeds(totalBeds);
//
//            return Result.success(stats);
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    public static class BedStats {
//        private Integer availableBeds;
//        private Integer occupiedBeds;
//        private Integer totalBeds;
//
//        // getters and setters
//        public Integer getAvailableBeds() { return availableBeds; }
//        public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }
//        public Integer getOccupiedBeds() { return occupiedBeds; }
//        public void setOccupiedBeds(Integer occupiedBeds) { this.occupiedBeds = occupiedBeds; }
//        public Integer getTotalBeds() { return totalBeds; }
//        public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }
//    }
//}