package com.dormitory.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.FeeDTO;
import com.dormitory.management.dto.FeePageDTO;
import com.dormitory.management.entity.Fee;
import com.dormitory.management.vo.FeeVO;

/**
 * 费用管理Service接口
 */
public interface FeeService extends IService<Fee> {

    /**
     * 分页查询费用列表
     *
     * @param pageDTO 分页查询参数
     * @return 分页结果
     */
    IPage<FeeVO> pageFees(FeePageDTO pageDTO);

    /**
     * 根据ID获取费用详情
     *
     * @param id 费用ID
     * @return 费用详情
     */
    FeeVO getFeeById(Long id);

    /**
     * 创建费用
     *
     * @param feeDTO 费用数据
     * @return 创建结果
     */
    boolean createFee(FeeDTO feeDTO);

    /**
     * 更新费用
     *
     * @param id 费用ID
     * @param feeDTO 费用数据
     * @return 更新结果
     */
    boolean updateFee(Long id, FeeDTO feeDTO);

    /**
     * 删除费用
     *
     * @param id 费用ID
     * @return 删除结果
     */
    boolean deleteFee(Long id);

    /**
     * 批量删除费用
     *
     * @param ids 费用ID列表
     * @return 删除结果
     */
    boolean batchDeleteFees(java.util.List<Long> ids);

    /**
     * 更新支付状态
     *
     * @param id 费用ID
     * @param paymentStatus 支付状态
     * @param paidAmount 已付金额
     * @return 更新结果
     */
    boolean updatePaymentStatus(Long id, Integer paymentStatus, java.math.BigDecimal paidAmount);

    /**
     * 统计费用汇总信息
     *
     * @param pageDTO 查询条件
     * @return 统计信息
     */
    java.util.Map<String, Object> getFeeStatistics(FeePageDTO pageDTO);
}