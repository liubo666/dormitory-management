package com.dormitory.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.DormitoryDTO;
import com.dormitory.management.dto.DormitoryPageDTO;
import com.dormitory.management.dto.DormitoryUpdateDTO;
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
    @PostMapping("/update")
    public Result<Void> updateDormitory(@Validated @RequestBody DormitoryUpdateDTO dormitoryDTO) {
        try {
            DormitoryDTO dormitoryDTOForService = new DormitoryDTO();
            dormitoryDTOForService.setId(dormitoryDTO.getId());
            dormitoryDTOForService.setRoomNo(dormitoryDTO.getRoomNo());
            dormitoryDTOForService.setDescription(dormitoryDTO.getRemark());
            boolean success = dormitoryService.createDormitory(dormitoryDTOForService);
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
}