package com.dormitory.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.DormitorySearchDTO;
import com.dormitory.management.dto.StudentSearchDTO;
import com.dormitory.management.dto.VisitorDTO;
import com.dormitory.management.dto.VisitorPageDTO;
import com.dormitory.management.service.VisitorService;
import com.dormitory.management.vo.DormitorySearchVO;
import com.dormitory.management.vo.StudentSearchVO;
import com.dormitory.management.vo.VisitorVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 访客登记控制器
 */
@Slf4j
@RestController
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    /**
     * 分页查询访客登记记录
     */
    @PostMapping("/page")
    public Result<IPage<VisitorVO>> getVisitorPage(@RequestBody VisitorPageDTO pageDTO) {
        try {
            IPage<VisitorVO> result = visitorService.getVisitorPage(pageDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取访客登记分页数据失败", e);
            return Result.error("获取访客登记分页数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询访客登记详情
     */
    @GetMapping("/{id}")
    public Result<VisitorVO> getVisitorById(@PathVariable Long id) {
        try {
            VisitorVO visitor = visitorService.getVisitorById(id);
            if (visitor == null) {
                return Result.error("访客记录不存在");
            }
            return Result.success(visitor);
        } catch (Exception e) {
            log.error("获取访客登记详情失败，ID: {}", id, e);
            return Result.error("获取访客登记详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增访客登记记录
     */
    @PostMapping("/add")
    public Result<String> addVisitor(@RequestBody VisitorDTO visitorDTO) {
        try {
            boolean success = visitorService.addVisitor(visitorDTO, null);
            if (success) {
                return Result.success("新增访客登记成功");
            } else {
                return Result.error("新增访客登记失败");
            }
        } catch (Exception e) {
            log.error("新增访客登记失败", e);
            return Result.error("新增访客登记失败: " + e.getMessage());
        }
    }

    /**
     * 更新访客登记记录
     */
    @PutMapping
    public Result<String> updateVisitor(@RequestBody VisitorDTO visitorDTO) {
        try {
            boolean success = visitorService.updateVisitor(visitorDTO, null);
            if (success) {
                return Result.success("更新访客登记成功");
            } else {
                return Result.error("更新访客登记失败");
            }
        } catch (Exception e) {
            log.error("更新访客登记失败", e);
            return Result.error("更新访客登记失败: " + e.getMessage());
        }
    }

    /**
     * 删除访客登记记录
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteVisitor(@PathVariable Long id) {
        try {
            boolean success = visitorService.deleteVisitor(id);
            if (success) {
                return Result.success("删除访客登记成功");
            } else {
                return Result.error("删除访客登记失败");
            }
        } catch (Exception e) {
            log.error("删除访客登记失败，ID: {}", id, e);
            return Result.error("删除访客登记失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除访客登记记录
     */
    @DeleteMapping("/batch")
    public Result<Map<String, Object>> batchDeleteVisitor(@RequestBody List<Long> ids) {
        try {
            Map<String, Object> result = visitorService.batchDeleteVisitor(ids);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量删除访客登记失败", e);
            return Result.error("批量删除访客登记失败: " + e.getMessage());
        }
    }

    /**
     * 访客签到
     */
    @PostMapping("/{id}/check-in")
    public Result<String> checkIn(@PathVariable Long id) {
        try {
            boolean success = visitorService.checkIn(id);
            if (success) {
                return Result.success("访客签到成功");
            } else {
                return Result.error("访客签到失败");
            }
        } catch (Exception e) {
            log.error("访客签到失败，ID: {}", id, e);
            return Result.error("访客签到失败: " + e.getMessage());
        }
    }

    /**
     * 访客签退
     */
    @PostMapping("/{id}/check-out")
    public Result<String> checkOut(@PathVariable Long id) {
        try {
            boolean success = visitorService.checkOut(id);
            if (success) {
                return Result.success("访客签退成功");
            } else {
                return Result.error("访客签退失败");
            }
        } catch (Exception e) {
            log.error("访客签退失败，ID: {}", id, e);
            return Result.error("访客签退失败: " + e.getMessage());
        }
    }

    /**
     * 取消访客访问
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancelVisit(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            boolean success = visitorService.cancelVisit(id, reason);
            if (success) {
                return Result.success("取消访客访问成功");
            } else {
                return Result.error("取消访客访问失败");
            }
        } catch (Exception e) {
            log.error("取消访客访问失败，ID: {}", id, e);
            return Result.error("取消访客访问失败: " + e.getMessage());
        }
    }

    /**
     * 获取宿舍选项列表
     */
    @GetMapping("/dormitories/options")
    public Result<List<Map<String, Object>>> getDormitoryOptions() {
        try {
            List<Map<String, Object>> options = visitorService.getDormitoryOptions();
            return Result.success(options);
        } catch (Exception e) {
            log.error("获取宿舍选项失败", e);
            return Result.error("获取宿舍选项失败: " + e.getMessage());
        }
    }

    /**
     * 分页搜索宿舍信息（支持模糊查询）
     */
    @PostMapping("/dormitories/search")
    public Result<IPage<DormitorySearchVO>> searchDormitories(@RequestBody DormitorySearchDTO searchDTO) {
        try {
            IPage<DormitorySearchVO> result = visitorService.searchDormitories(searchDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索宿舍信息失败", e);
            return Result.error("搜索宿舍信息失败: " + e.getMessage());
        }
    }

    /**
     * 分页搜索学生信息（支持模糊查询）
     */
    @PostMapping("/students/search")
    public Result<IPage<StudentSearchVO>> searchStudents(@RequestBody StudentSearchDTO searchDTO) {
        try {
            IPage<StudentSearchVO> result = visitorService.searchStudents(searchDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索学生信息失败", e);
            return Result.error("搜索学生信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取访客统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getVisitorStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> statistics = visitorService.getVisitorStatistics(startDate, endDate);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取访客统计信息失败", e);
            return Result.error("获取访客统计信息失败: " + e.getMessage());
        }
    }
}