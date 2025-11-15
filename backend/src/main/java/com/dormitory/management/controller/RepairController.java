package com.dormitory.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.RepairDTO;
import com.dormitory.management.dto.RepairHandleDTO;
import com.dormitory.management.dto.RepairPageDTO;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.service.RepairService;
import com.dormitory.management.service.StudentService;
import com.dormitory.management.vo.DormitoryVO;
import com.dormitory.management.vo.RepairVO;
import com.dormitory.management.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报修管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
@Tag(name = "报修管理", description = "报修管理相关接口")
public class RepairController {

    private final RepairService repairService;
    private final DormitoryService dormitoryService;
    private final StudentService studentService;

    @Operation(summary = "分页查询报修列表")
    @PostMapping("/page")
    public Result<IPage<RepairVO>> page(@RequestBody RepairPageDTO params) {
        try {
            IPage<RepairVO> result = repairService.getRepairPage(params);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询报修列表失败", e);
            return Result.error("查询报修列表失败");
        }
    }

    @Operation(summary = "根据ID查询报修详情")
    @GetMapping("/{id}")
    public Result<RepairVO> getById(@PathVariable Long id) {
        try {
            RepairVO repairVO = repairService.getRepairById(id);
            if (repairVO == null) {
                return Result.error("报修记录不存在");
            }
            return Result.success(repairVO);
        } catch (Exception e) {
            log.error("查询报修详情失败", e);
            return Result.error("查询报修详情失败");
        }
    }

    @Operation(summary = "新增报修")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody RepairDTO repairDTO) {
        try {
            boolean success = repairService.addRepair(repairDTO);
            if (success) {
                return Result.success();
            }
            return Result.error("新增报修失败");
        } catch (Exception e) {
            log.error("新增报修失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "处理报修")
    @PostMapping("/handle")
    public Result<Void> handle(@Validated @RequestBody RepairHandleDTO handleDTO) {
        try {
            boolean success = repairService.handleRepair(handleDTO);
            if (success) {
                return Result.success();
            }
            return Result.error("处理报修失败");
        } catch (Exception e) {
            log.error("处理报修失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除报修")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = repairService.deleteRepair(id);
            if (success) {
                return Result.success();
            }
            return Result.error("删除报修失败");
        } catch (Exception e) {
            log.error("删除报修失败", e);
            return Result.error("删除报修失败");
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

    @Operation(summary = "获取学生选项列表")
    @GetMapping("/students/options")
    public Result<List<StudentVO>> getStudentOptions(
            @RequestParam(required = false) String keyword) {
        try {
            List<StudentVO> students = studentService.getActiveStudents(keyword);
            return Result.success(students);
        } catch (Exception e) {
            log.error("获取学生选项失败", e);
            return Result.error("获取学生选项失败");
        }
    }
}