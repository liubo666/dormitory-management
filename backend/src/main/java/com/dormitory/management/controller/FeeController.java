package com.dormitory.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dormitory.management.common.Result;
import com.dormitory.management.dto.FeeDTO;
import com.dormitory.management.dto.FeePageDTO;
import com.dormitory.management.dto.FeeUpdateDTO;
import com.dormitory.management.dto.PaymentStatusUpdateDTO;
import com.dormitory.management.dto.PartialPaymentDTO;
import com.dormitory.management.service.FeeService;
import com.dormitory.management.vo.FeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 费用管理Controller
 */
@RestController
@RequestMapping("/api/fees")
public class FeeController {

    @Autowired
    private FeeService feeService;

    /**
     * 分页查询费用列表
     */
    @PostMapping("/page")
    public Result<IPage<FeeVO>> pageFees(@RequestBody FeePageDTO pageDTO) {
        IPage<FeeVO> result = feeService.pageFees(pageDTO);
        return Result.success(result);
    }

    /**
     * 根据ID获取费用详情
     */
    @GetMapping("/{id}")
    public Result<FeeVO> getFeeById(@PathVariable Long id) {
        FeeVO feeVO = feeService.getFeeById(id);
        if (feeVO == null) {
            return Result.error("费用不存在");
        }
        return Result.success(feeVO);
    }

    /**
     * 创建费用
     */
    @PostMapping("/add")
    public Result<Boolean> createFee(@RequestBody FeeDTO feeDTO) {
        boolean result = feeService.createFee(feeDTO);
        return result ? Result.success(true) : Result.error("创建费用失败");
    }

    /**
     * 更新费用
     */
    @PostMapping("/update")
    public Result<Boolean> updateFee(@RequestBody FeeUpdateDTO feeUpdateDTO) {
        FeeDTO feeDTO = new FeeDTO();
        feeDTO.setStudentId(feeUpdateDTO.getStudentId());
        feeDTO.setRoomId(feeUpdateDTO.getDormitoryId());
        feeDTO.setFeeType(feeUpdateDTO.getFeeType().equals("住宿费") ? 1 :
                         feeUpdateDTO.getFeeType().equals("水电费") ? 2 :
                         feeUpdateDTO.getFeeType().equals("网费") ? 3 : 4);
        feeDTO.setAmount(feeUpdateDTO.getAmount());
        feeDTO.setPaymentStatus(feeUpdateDTO.getPaymentStatus());
        feeDTO.setRemark(feeUpdateDTO.getRemark());
        boolean result = feeService.updateFee(feeUpdateDTO.getId(), feeDTO);
        return result ? Result.success(true) : Result.error("更新费用失败");
    }

    /**
     * 删除费用
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteFee(@PathVariable Long id) {
        boolean result = feeService.deleteFee(id);
        return result ? Result.success(true) : Result.error("删除费用失败");
    }

    /**
     * 批量删除费用
     */
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteFees(@RequestBody List<Long> ids) {
        boolean result = feeService.batchDeleteFees(ids);
        return result ? Result.success(true) : Result.error("批量删除费用失败");
    }

    /**
     * 更新支付状态
     */
    @PostMapping("/payment-status")
    public Result<Boolean> updatePaymentStatus(@RequestBody PaymentStatusUpdateDTO updateDTO) {
        boolean result = feeService.updatePaymentStatus(updateDTO.getId(), updateDTO.getPaymentStatus(), updateDTO.getPaidAmount());
        return result ? Result.success(true) : Result.error("更新支付状态失败");
    }

    /**
     * 获取费用统计信息
     */
    @PostMapping("/statistics")
    public Result<Map<String, Object>> getFeeStatistics(@RequestBody FeePageDTO pageDTO) {
        Map<String, Object> statistics = feeService.getFeeStatistics(pageDTO);
        return Result.success(statistics);
    }

    /**
     * 标记为已支付
     */
    @PostMapping("/paid")
    public Result<Boolean> markAsPaid(@RequestBody PaymentStatusUpdateDTO updateDTO) {
        FeeVO feeVO = feeService.getFeeById(updateDTO.getId());
        if (feeVO == null) {
            return Result.error("费用不存在");
        }

        boolean result = feeService.updatePaymentStatus(updateDTO.getId(), 2, feeVO.getAmount());
        return result ? Result.success(true) : Result.error("标记为已支付失败");
    }

    /**
     * 部分支付
     */
    @PostMapping("/partial-payment")
    public Result<Boolean> partialPayment(@RequestBody PartialPaymentDTO paymentDTO) {
        FeeVO feeVO = feeService.getFeeById(paymentDTO.getId());
        if (feeVO == null) {
            return Result.error("费用不存在");
        }

        BigDecimal totalPaid = feeVO.getPaidAmount().add(paymentDTO.getPaymentAmount());
        Integer paymentStatus;
        if (totalPaid.compareTo(feeVO.getAmount()) >= 0) {
            paymentStatus = 2; // 已支付
            totalPaid = feeVO.getAmount(); // 防止超额支付
        } else {
            paymentStatus = 1; // 部分支付
        }

        boolean result = feeService.updatePaymentStatus(paymentDTO.getId(), paymentStatus, totalPaid);
        return result ? Result.success(true) : Result.error("部分支付失败");
    }
}