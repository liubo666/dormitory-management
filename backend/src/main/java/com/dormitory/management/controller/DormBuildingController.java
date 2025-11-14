package com.dormitory.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.BuildingDTO;
import com.dormitory.management.dto.BuildingPageDTO;
import com.dormitory.management.entity.DormBuilding;
import com.dormitory.management.service.DormBuildingService;
import com.dormitory.management.vo.BuildingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 宿舍楼控制器
 */
@Tag(name = "宿舍楼管理", description = "宿舍楼相关接口")
@RestController
@RequestMapping("/building")
public class DormBuildingController {

    @Autowired
    private DormBuildingService dormBuildingService;

    @Operation(summary = "分页查询宿舍楼列表")
    @PostMapping("/page")
    public Result<IPage<BuildingVO>> getBuildingPage(@RequestBody BuildingPageDTO pageDTO) {
        Page<DormBuilding> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        IPage<BuildingVO> result = dormBuildingService.getBuildingPage(
                page,
                pageDTO.getBuildingNo(),
                pageDTO.getBuildingName(),
                pageDTO.getStatus()
        );
        return Result.success(result);
    }

    @Operation(summary = "根据ID获取宿舍楼详情")
    @GetMapping("/{id}")
    public Result<BuildingVO> getBuildingById(
            @Parameter(description = "楼栋ID", required = true) @PathVariable Long id) {

        BuildingVO building = dormBuildingService.getBuildingById(id);
        if (building == null) {
            return Result.error("宿舍楼不存在");
        }
        return Result.success(building);
    }

    @Operation(summary = "新增宿舍楼")
    @PostMapping
    public Result<Void> createBuilding(@Validated @RequestBody BuildingDTO buildingDTO) {
        try {
            dormBuildingService.createBuilding(buildingDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新宿舍楼")
    @PutMapping("/{id}")
    public Result<Void> updateBuilding(
            @Parameter(description = "楼栋ID", required = true) @PathVariable Long id,
            @Validated @RequestBody BuildingDTO buildingDTO) {

        buildingDTO.setId(id);
        try {
            dormBuildingService.updateBuilding(buildingDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除宿舍楼")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBuilding(
            @Parameter(description = "楼栋ID", required = true) @PathVariable String id) {

        try {
            dormBuildingService.deleteBuilding(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "修改宿舍楼状态")
    @GetMapping("/status")
    public Result<Void> updateBuildingStatus(
            @Parameter(description = "楼栋ID", required = true) @RequestParam("id") String id,
            @Parameter(description = "状态", required = true) @RequestParam("status") Integer status) {

        try {
            dormBuildingService.updateBuildingStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}