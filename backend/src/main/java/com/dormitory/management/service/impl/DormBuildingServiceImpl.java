package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.dto.BuildingDTO;
import com.dormitory.management.entity.DormBuilding;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.mapper.DormBuildingMapper;
import com.dormitory.management.service.DormBuildingService;
import com.dormitory.management.service.SysUserService;
import com.dormitory.management.vo.BuildingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 宿舍楼Service实现类
 */
@Slf4j
@Service
public class DormBuildingServiceImpl extends ServiceImpl<DormBuildingMapper, DormBuilding> implements DormBuildingService {


    @Autowired
    private SysUserService sysUserService;

    @Override
    public IPage<BuildingVO> getBuildingPage(Page<DormBuilding> page, String buildingNo, String buildingName, Integer status) {
        LambdaQueryWrapper<DormBuilding> wrapper = new LambdaQueryWrapper<>();

        // 查询条件
        if (StringUtils.hasText(buildingNo)) {
            wrapper.like(DormBuilding::getBuildingNo, buildingNo);
        }
        if (StringUtils.hasText(buildingName)) {
            wrapper.like(DormBuilding::getBuildingName, buildingName);
        }
        if (status != null) {
            wrapper.eq(DormBuilding::getStatus, status);
        }

        wrapper.orderByDesc(DormBuilding::getCreateTime);

        IPage<DormBuilding> buildingPage = page(page, wrapper);

        // 转换为VO
        return buildingPage.convert(this::convertToVO);
    }

    @Override
    public BuildingVO getBuildingById(Long id) {
        DormBuilding building = getById(id);
        if (building == null) {
            return null;
        }
        return convertToVO(building);
    }

    @Override
    public boolean createBuilding(BuildingDTO buildingDTO) {
        // 检查楼栋号是否已存在
        LambdaQueryWrapper<DormBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormBuilding::getBuildingNo, buildingDTO.getBuildingNo());
        DormBuilding existingBuilding = getOne(wrapper);

        if (existingBuilding != null) {
            throw new RuntimeException("楼栋号已存在");
        }

        DormBuilding building = new DormBuilding();
        BeanUtils.copyProperties(buildingDTO, building);
        building.setCreateTime(LocalDateTime.now());
        building.setDeleted(0);
        String realName = UserContext.getCurrentRealName();
        building.setCreateBy(realName != null ? realName : "system");

        return save(building);
    }

    @Override
    public boolean updateBuilding(BuildingDTO buildingDTO) {
        DormBuilding building = getById(buildingDTO.getId());
        if (building == null) {
            throw new RuntimeException("宿舍楼不存在");
        }

        // 检查楼栋号是否被其他楼栋使用
        LambdaQueryWrapper<DormBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormBuilding::getBuildingNo, buildingDTO.getBuildingNo())
               .ne(DormBuilding::getId, buildingDTO.getId());
        DormBuilding existingBuilding = getOne(wrapper);

        if (existingBuilding != null) {
            throw new RuntimeException("楼栋号已存在");
        }

        BeanUtils.copyProperties(buildingDTO, building);
        building.setUpdateTime(LocalDateTime.now());
        String currentRealName = UserContext.getCurrentRealName();
        building.setUpdateBy(currentRealName != null ? currentRealName : "system");

        return updateById(building);
    }

    @Override
    public boolean deleteBuilding(String id) {
        DormBuilding building = getById(id);
        if (building == null) {
            throw new RuntimeException("宿舍楼不存在");
        }

        return removeById(id);
    }

    @Override
    public boolean updateBuildingStatus(String id, Integer status) {
        DormBuilding building = getById(id);
        if (building == null) {
            throw new RuntimeException("宿舍楼不存在");
        }

        building.setStatus(status);
        building.setUpdateTime(LocalDateTime.now());
        String currentRealName = UserContext.getCurrentRealName();
        building.setUpdateBy(currentRealName != null ? currentRealName : "system");

        return updateById(building);
    }

    /**
     * 实体转VO
     */
    private BuildingVO convertToVO(DormBuilding building) {
        BuildingVO vo = new BuildingVO();
        BeanUtils.copyProperties(building, vo);
        vo.setStatusText(building.getStatus() == 1 ? "启用" : "停用");
        return vo;
    }
}