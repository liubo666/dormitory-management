package com.dormitory.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dormitory.management.dto.DormitorySearchDTO;
import com.dormitory.management.dto.StudentSearchDTO;
import com.dormitory.management.dto.VisitorDTO;
import com.dormitory.management.dto.VisitorPageDTO;
import com.dormitory.management.vo.DormitorySearchVO;
import com.dormitory.management.vo.StudentSearchVO;
import com.dormitory.management.vo.VisitorVO;

import java.util.List;
import java.util.Map;

/**
 * 访客登记服务接口
 */
public interface VisitorService {

    /**
     * 分页查询访客登记记录
     */
    IPage<VisitorVO> getVisitorPage(VisitorPageDTO pageDTO);

    /**
     * 根据ID查询访客登记详情
     */
    VisitorVO getVisitorById(Long id);

    /**
     * 新增访客登记记录
     */
    boolean addVisitor(VisitorDTO visitorDTO, String createBy);

    /**
     * 更新访客登记记录
     */
    boolean updateVisitor(VisitorDTO visitorDTO, String updateBy);

    /**
     * 删除访客登记记录
     */
    boolean deleteVisitor(Long id);

    /**
     * 批量删除访客登记记录
     */
    Map<String, Object> batchDeleteVisitor(List<Long> ids);

    /**
     * 签到（更新实际到达时间，状态改为访问中）
     */
    boolean checkIn(Long id);

    /**
     * 签退（更新离开时间，状态改为已完成）
     */
    boolean checkOut(Long id);

    /**
     * 取消访问（状态改为已取消）
     */
    boolean cancelVisit(Long id, String reason);

    /**
     * 获取宿舍选项列表
     */
    List<Map<String, Object>> getDormitoryOptions();

    /**
     * 分页搜索宿舍信息（支持模糊查询）
     */
    IPage<DormitorySearchVO> searchDormitories(DormitorySearchDTO searchDTO);

    /**
     * 获取访客统计信息
     */
    Map<String, Object> getVisitorStatistics(String startDate, String endDate);

    /**
     * 分页搜索学生信息（支持模糊查询）
     */
    IPage<StudentSearchVO> searchStudents(StudentSearchDTO searchDTO);
}