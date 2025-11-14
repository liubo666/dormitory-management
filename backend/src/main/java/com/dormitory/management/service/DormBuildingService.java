package com.dormitory.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.BuildingDTO;
import com.dormitory.management.entity.DormBuilding;
import com.dormitory.management.vo.BuildingVO;

/**
 * 宿舍楼Service接口
 */
public interface DormBuildingService extends IService<DormBuilding> {

    /**
     * 分页查询宿舍楼列表
     *
     * @param page 分页参数
     * @param buildingNo 楼栋号
     * @param buildingName 楼栋名称
     * @param status 状态
     * @return 分页结果
     */
    IPage<BuildingVO> getBuildingPage(Page<DormBuilding> page, String buildingNo, String buildingName, Integer status);

    /**
     * 根据ID获取宿舍楼详情
     *
     * @param id 楼栋ID
     * @return 宿舍楼详情
     */
    BuildingVO getBuildingById(Long id);

    /**
     * 新增宿舍楼
     *
     * @param buildingDTO 宿舍楼信息
     * @return 是否成功
     */
    boolean createBuilding(BuildingDTO buildingDTO);

    /**
     * 更新宿舍楼
     *
     * @param buildingDTO 宿舍楼信息
     * @return 是否成功
     */
    boolean updateBuilding(BuildingDTO buildingDTO);

    /**
     * 删除宿舍楼
     *
     * @param id 楼栋ID
     * @return 是否成功
     */
    boolean deleteBuilding(String id);

    /**
     * 修改宿舍楼状态
     *
     * @param id 楼栋ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateBuildingStatus(String id, Integer status);
}