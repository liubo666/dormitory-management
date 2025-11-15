package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.InspectionDTO;
import com.dormitory.management.dto.InspectionPageDTO;
import com.dormitory.management.entity.Dormitory;
import com.dormitory.management.entity.Inspection;
import com.dormitory.management.enums.InspectionLevelEnum;
import com.dormitory.management.mapper.InspectionMapper;
import com.dormitory.management.service.DormitoryService;
import com.dormitory.management.service.InspectionService;
import org.springframework.util.StringUtils;
import com.dormitory.management.vo.InspectionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 卫生检查服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionServiceImpl extends ServiceImpl<InspectionMapper, Inspection> implements InspectionService {

    private final DormitoryService dormitoryService;

    @Override
    public IPage<InspectionVO> getInspectionPage(InspectionPageDTO pageDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Inspection> queryWrapper = new LambdaQueryWrapper<>();

        // 宿舍ID条件
        if (pageDTO.getRoomId() != null) {
            queryWrapper.eq(Inspection::getRoomId, pageDTO.getRoomId());
        }

        // 等级条件
        if (StringUtils.hasText(pageDTO.getLevel())) {
            queryWrapper.eq(Inspection::getLevel, pageDTO.getLevel());
        }

//        // 分数范围条件
//        if (pageDTO.getMinScore() != null) {
//            queryWrapper.ge(Inspection::getScore, pageDTO.getMinScore());
//        }
//        if (pageDTO.getMaxScore() != null) {
//            queryWrapper.le(Inspection::getScore, pageDTO.getMaxScore());
//        }

        // 日期范围条件
        if (StringUtils.hasText(pageDTO.getStartDate())) {
            queryWrapper.ge(Inspection::getInspectionDate,
                LocalDateTime.parse(pageDTO.getStartDate() + " 00:00:00"));
        }
        if (StringUtils.hasText(pageDTO.getEndDate())) {
            queryWrapper.le(Inspection::getInspectionDate,
                LocalDateTime.parse(pageDTO.getEndDate() + " 23:59:59"));
        }

        // 按检查日期降序排序
        queryWrapper.orderByDesc(Inspection::getInspectionDate)
                   .orderByDesc(Inspection::getCreateTime);

        // 执行分页查询
        Page<Inspection> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        Page<Inspection> result = this.page(page, queryWrapper);

        // 转换为VO对象
        List<InspectionVO> voList = result.getRecords().stream().map(inspection -> {
            InspectionVO vo = new InspectionVO();
            BeanUtils.copyProperties(inspection, vo);

            // 获取宿舍信息
            Dormitory dormitory = dormitoryService.getById(inspection.getRoomId());
            if (dormitory != null) {
                vo.setRoomNo(dormitory.getRoomNo());
                vo.setBuildingName(dormitory.getBuildingName());
            }

            // 设置等级文本和图片列表
            setInspectionDisplayText(vo);
            if (StringUtils.hasText(inspection.getImages())) {
                vo.setImageList(Arrays.asList(inspection.getImages().split(",")));
            }

            return vo;
        }).collect(Collectors.toList());

        // 构建返回结果
        Page<InspectionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public InspectionVO getInspectionById(Long id) {
        Inspection inspection = getById(id);
        if (inspection == null) {
            return null;
        }

        InspectionVO vo = new InspectionVO();
        BeanUtils.copyProperties(inspection, vo);

        // 获取宿舍信息
        Dormitory dormitory = dormitoryService.getById(inspection.getRoomId());
        if (dormitory != null) {
            vo.setRoomNo(dormitory.getRoomNo());
            vo.setBuildingName(dormitory.getBuildingName());
        }

        // 设置等级文本和图片列表
        setInspectionDisplayText(vo);
        if (StringUtils.hasText(inspection.getImages())) {
            vo.setImageList(Arrays.asList(inspection.getImages().split(",")));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addInspection(InspectionDTO inspectionDTO, String createBy) {
        try {
            // 验证宿舍是否存在
            Dormitory dormitory = dormitoryService.getById(inspectionDTO.getRoomId());
            if (dormitory == null) {
                throw new RuntimeException("宿舍不存在");
            }

            Inspection inspection = new Inspection();
            BeanUtils.copyProperties(inspectionDTO, inspection);

            // 解析检查日期
            if (StringUtils.hasText(inspectionDTO.getInspectionDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime inspectionDate = LocalDateTime.parse(inspectionDTO.getInspectionDate(), formatter);
                inspection.setInspectionDate(inspectionDate);
            }

            // 根据分数自动设置等级
            if (inspectionDTO.getScore() != null) {
                String level = InspectionLevelEnum.getLevelByScore(inspectionDTO.getScore());
                inspection.setLevel(level);
            }

            // 设置检查人信息
            inspection.setInspectorId(UserContext.getCurrentUserId());
            inspection.setInspectorName(UserContext.getCurrentRealName());

            // 处理图片列表
            if (inspectionDTO.getImages() != null && !inspectionDTO.getImages().isEmpty()) {
                inspection.setImages(String.join(",", inspectionDTO.getImages()));
            }

            boolean result = save(inspection);
            if (result) {
                log.info("新增卫生检查记录成功，宿舍ID: {}", inspectionDTO.getRoomId());
            }
            return result;
        } catch (Exception e) {
            log.error("新增卫生检查记录失败", e);
            throw new RuntimeException("新增卫生检查记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInspection(InspectionDTO inspectionDTO, String updateBy) {
        try {
            Inspection inspection = getById(inspectionDTO.getId());
            if (inspection == null) {
                throw new RuntimeException("检查记录不存在");
            }

            // 验证宿舍是否存在
            Dormitory dormitory = dormitoryService.getById(inspectionDTO.getRoomId());
            if (dormitory == null) {
                throw new RuntimeException("宿舍不存在");
            }

            BeanUtils.copyProperties(inspectionDTO, inspection);

            // 解析检查日期
            if (StringUtils.hasText(inspectionDTO.getInspectionDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime inspectionDate = LocalDateTime.parse(inspectionDTO.getInspectionDate(), formatter);
                inspection.setInspectionDate(inspectionDate);
            }

            // 根据分数自动设置等级
            if (inspectionDTO.getScore() != null) {
                String level = InspectionLevelEnum.getLevelByScore(inspectionDTO.getScore());
                inspection.setLevel(level);
            }

            // 处理图片列表
            if (inspectionDTO.getImages() != null && !inspectionDTO.getImages().isEmpty()) {
                inspection.setImages(String.join(",", inspectionDTO.getImages()));
            } else {
                inspection.setImages(null);
            }

            boolean result = updateById(inspection);
            if (result) {
                log.info("更新卫生检查记录成功，ID: {}", inspectionDTO.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新卫生检查记录失败", e);
            throw new RuntimeException("更新卫生检查记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInspection(Long id) {
        try {
            Inspection inspection = getById(id);
            if (inspection == null) {
                throw new RuntimeException("检查记录不存在");
            }

            boolean result = removeById(id);
            if (result) {
                log.info("删除卫生检查记录成功，ID: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除卫生检查记录失败", e);
            throw new RuntimeException("删除卫生检查记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchDeleteInspection(List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;

        for (Long id : ids) {
            try {
                if (deleteInspection(id)) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
                log.error("删除检查记录失败，ID: {}", id, e);
            }
        }

        result.put("totalCount", ids.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("success", failCount == 0);

        return result;
    }

    @Override
    public List<InspectionVO> getInspectionHistoryByRoomId(Long roomId, Integer limit) {
        // 使用通用的查询方法，简化实现
        InspectionPageDTO pageDTO = new InspectionPageDTO();
        pageDTO.setRoomId(roomId);
        pageDTO.setSize(limit != null ? limit : 10);
        pageDTO.setCurrent(1);

        IPage<InspectionVO> page = getInspectionPage(pageDTO);
        return page.getRecords();
    }

    @Override
    public Map<String, Object> getInspectionStatistics(String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();

        // 这里可以根据需要实现统计逻辑
        // 例如：总检查次数、各等级数量、平均分等

        return statistics;
    }

    @Override
    public List<InspectionVO> getFailedInspections(Integer days) {
        // 使用通用的查询方法，查询不合格的检查记录
        InspectionPageDTO pageDTO = new InspectionPageDTO();
        pageDTO.setLevel("fail");
        pageDTO.setSize(100); // 设置一个较大的限制
        pageDTO.setCurrent(1);

        IPage<InspectionVO> page = getInspectionPage(pageDTO);
        return page.getRecords();
    }

    /**
     * 设置检查记录显示文本
     */
    private void setInspectionDisplayText(InspectionVO inspection) {
        if (StringUtils.hasText(inspection.getLevel())) {
            inspection.setLevelText(InspectionLevelEnum.getDescriptionByCode(inspection.getLevel()));
        }
    }
}