package com.dormitory.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.InspectionDTO;
import com.dormitory.management.dto.InspectionPageDTO;
import com.dormitory.management.entity.Inspection;
import com.dormitory.management.vo.InspectionVO;

import java.util.List;
import java.util.Map;

/**
 * 卫生检查服务接口
 */
public interface InspectionService extends IService<Inspection> {

    /**
     * 分页查询卫生检查记录
     *
     * @param pageDTO 查询条件
     * @return 分页结果
     */
    IPage<InspectionVO> getInspectionPage(InspectionPageDTO pageDTO);

    /**
     * 根据ID获取检查详情
     *
     * @param id 检查记录ID
     * @return 检查详情
     */
    InspectionVO getInspectionById(Long id);

    /**
     * 新增卫生检查记录
     *
     * @param inspectionDTO 检查信息
     * @param createBy 创建人
     * @return 是否成功
     */
    boolean addInspection(InspectionDTO inspectionDTO, String createBy);

    /**
     * 更新卫生检查记录
     *
     * @param inspectionDTO 检查信息
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean updateInspection(InspectionDTO inspectionDTO, String updateBy);

    /**
     * 删除卫生检查记录
     *
     * @param id 检查记录ID
     * @return 是否成功
     */
    boolean deleteInspection(Long id);

    /**
     * 批量删除检查记录
     *
     * @param ids 检查记录ID列表
     * @return 删除结果
     */
    Map<String, Object> batchDeleteInspection(List<Long> ids);

    /**
     * 获取宿舍的检查历史
     *
     * @param roomId 宿舍ID
     * @param limit 限制数量
     * @return 检查记录列表
     */
    List<InspectionVO> getInspectionHistoryByRoomId(Long roomId, Integer limit);

    /**
     * 获取卫生检查统计信息
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    Map<String, Object> getInspectionStatistics(String startDate, String endDate);

    /**
     * 获取不合格宿舍列表
     *
     * @param days 天数
     * @return 宿舍列表
     */
    List<InspectionVO> getFailedInspections(Integer days);
}