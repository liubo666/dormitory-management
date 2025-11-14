package com.dormitory.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.CheckInDTO;
import com.dormitory.management.dto.CheckInPageDTO;
import com.dormitory.management.dto.AvailableStudentDTO;
import com.dormitory.management.dto.AvailableBedDTO;
import com.dormitory.management.entity.CheckIn;
import com.dormitory.management.vo.CheckInVO;

import java.util.List;
import java.util.Map;

/**
 * 入住分配Service接口
 */
public interface CheckInService extends IService<CheckIn> {

    /**
     * 分页查询入住分配列表
     */
    Page<CheckInVO> getCheckInPage(Page<CheckInVO> page, CheckInPageDTO pageDTO);

    /**
     * 根据ID获取入住分配详情
     */
    CheckInVO getCheckInById(String id);

    /**
     * 提交入住申请
     */
    boolean submitCheckInApplication(CheckInDTO checkInDTO, String createBy);

    /**
     * 审批入住申请
     */
    boolean approveCheckIn(String id, Integer status, String approvalRemark, String approver);

    /**
     * 分配宿舍
     */
    boolean assignDormitory(String checkInId, String dormitoryId, String bedId, String bedNo, String updateBy);

    /**
     * 退宿处理
     */
    boolean checkout(String checkInId, String checkoutReason, String updateBy);

    /**
     * 取消入住申请
     */
    boolean cancelApplication(String checkInId, String reason, String updateBy);

    /**
     * 批量审批入住申请
     */
    Map<String, Object> batchApprove(List<String> ids, Integer status, String approvalRemark, String approver);

    /**
     * 获取入住统计信息
     */
    Map<String, Object> getCheckInStatistics();

    /**
     * 根据学生ID获取入住记录
     */
    CheckInVO getCheckInByStudentId(String studentId);

    /**
     * 根据宿舍ID获取入住学生列表
     */
    List<CheckInVO> getCheckInsByDormitoryId(String dormitoryId);

    /**
     * 根据床位ID获取入住记录
     */
    CheckInVO getCheckInByBedId(String bedId);

    /**
     * 获取可申请入住的学生列表（在校生且未入住）
     */
    List<AvailableStudentDTO> getAvailableStudents(String keyword);

    /**
     * 获取可用床位列表（有空闲床位的宿舍）
     */
    List<AvailableBedDTO> getAvailableBeds();
}