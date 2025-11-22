package com.dormitory.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.dto.RegistrationApplicationDTO;
import com.dormitory.management.vo.RegistrationApplicationVO;

/**
 * 注册申请服务接口
 */
public interface RegistrationApplicationService {

    /**
     * 提交注册申请
     */
    String submitApplication(RegistrationApplicationDTO dto);

    /**
     * 分页查询注册申请
     */
    IPage<RegistrationApplicationVO> getApplicationPage(Integer status);

    /**
     * 审批通过
     */
    void approveApplication(String approvalToken);


//    /**
//     * 审批驳回
//     */
//    void rejectApplication(String approvalToken, String rejectionReason);

    /**
     * 审批驳回（带权限验证）
     */
    void rejectApplication(String approvalToken, String rejectionReason);

    /**
     * 根据审批token获取申请详情
     */
    RegistrationApplicationVO getApplicationByToken(String approvalToken);

    /**
     * 检查管理员工号是否有效
     */
    boolean validateAdminEmployeeNo(String employeeNo);
}