package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.dto.FeeDTO;
import com.dormitory.management.dto.FeePageDTO;
import com.dormitory.management.entity.CheckIn;
import com.dormitory.management.entity.Fee;
import com.dormitory.management.entity.Student;
import com.dormitory.management.mapper.CheckInMapper;
import com.dormitory.management.mapper.FeeMapper;
import com.dormitory.management.mapper.StudentMapper;
import com.dormitory.management.service.FeeService;
import com.dormitory.management.vo.FeeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 费用管理Service实现类
 */
@Service
public class FeeServiceImpl extends ServiceImpl<FeeMapper, Fee> implements FeeService {

    private final StudentMapper studentMapper;
    private final CheckInMapper checkInMapper;

    public FeeServiceImpl(StudentMapper studentMapper, CheckInMapper checkInMapper) {
        this.studentMapper = studentMapper;
        this.checkInMapper = checkInMapper;
    }

    @Override
    public IPage<FeeVO> pageFees(FeePageDTO pageDTO) {
        // 创建分页对象
        Page<Fee> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<Fee> wrapper = new LambdaQueryWrapper<>();

        // 费用类型
        if (pageDTO.getFeeType() != null) {
            wrapper.eq(Fee::getFeeType, pageDTO.getFeeType());
        }

        // 费用名称模糊查询
        if (StringUtils.hasText(pageDTO.getFeeName())) {
            wrapper.like(Fee::getFeeName, pageDTO.getFeeName());
        }

        // 学生信息
        if (pageDTO.getStudentId() != null) {
            wrapper.eq(Fee::getStudentId, pageDTO.getStudentId());
        }
        if (StringUtils.hasText(pageDTO.getStudentName())) {
            wrapper.like(Fee::getStudentName, pageDTO.getStudentName());
        }
        if (StringUtils.hasText(pageDTO.getStudentNo())) {
            wrapper.like(Fee::getStudentNo, pageDTO.getStudentNo());
        }

        // 宿舍信息
        if (pageDTO.getRoomId() != null) {
            wrapper.eq(Fee::getRoomId, pageDTO.getRoomId());
        }
        if (StringUtils.hasText(pageDTO.getRoomNo())) {
            wrapper.like(Fee::getRoomNo, pageDTO.getRoomNo());
        }
        if (StringUtils.hasText(pageDTO.getBuildingName())) {
            wrapper.like(Fee::getBuildingName, pageDTO.getBuildingName());
        }

        // 支付状态
        if (pageDTO.getPaymentStatus() != null) {
            wrapper.eq(Fee::getPaymentStatus, pageDTO.getPaymentStatus());
        }

        // 收费员信息
        if (pageDTO.getCollectorId() != null) {
            wrapper.eq(Fee::getCollectorId, pageDTO.getCollectorId());
        }
        if (StringUtils.hasText(pageDTO.getCollectorName())) {
            wrapper.like(Fee::getCollectorName, pageDTO.getCollectorName());
        }

        // 日期范围查询
        if (pageDTO.getStartDate() != null) {
            wrapper.ge(Fee::getCreateTime, pageDTO.getStartDate());
        }
        if (pageDTO.getEndDate() != null) {
            wrapper.le(Fee::getCreateTime, pageDTO.getEndDate());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Fee::getCreateTime);

        // 执行查询
        IPage<Fee> feePage = this.page(page, wrapper);

        // 转换为VO对象
        IPage<FeeVO> feeVOPage = feePage.convert(this::convertToVO);

        return feeVOPage;
    }

    @Override
    public FeeVO getFeeById(Long id) {
        Fee fee = this.getById(id);
        if (fee == null) {
            return null;
        }
        return convertToVO(fee);
    }

    @Override
    public boolean createFee(FeeDTO feeDTO) {
        Fee fee = new Fee();
        BeanUtils.copyProperties(feeDTO, fee);

        // 获取学生信息并填充到费用记录中
        if (feeDTO.getStudentId() != null) {
            Student student = studentMapper.selectById(feeDTO.getStudentId());
            if (student != null) {
                fee.setStudentName(student.getName());
                fee.setStudentNo(student.getStudentNo());

                // 获取学生的住宿信息
                LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
                checkInWrapper.eq(CheckIn::getStudentId, feeDTO.getStudentId())
                             .eq(CheckIn::getStatus, 2) // 2:已入住
                             .orderByDesc(CheckIn::getCreateTime)
                             .last("LIMIT 1"); // 只获取最新的入住记录

                CheckIn checkIn = checkInMapper.selectOne(checkInWrapper);
                if (checkIn != null) {
                    fee.setRoomId(checkIn.getDormitoryId());
                    fee.setRoomNo(checkIn.getRoomNo());
                    fee.setBuildingName(checkIn.getBuildingName());
                }
            }
        }

        // 设置默认值
        fee.setCreateTime(LocalDateTime.now());
        fee.setUpdateTime(LocalDateTime.now());
        if (fee.getPaymentStatus() == null) {
            fee.setPaymentStatus(0); // 默认未支付
        }
        if (fee.getPaidAmount() == null) {
            fee.setPaidAmount(BigDecimal.ZERO);
        }

        return this.save(fee);
    }

    @Override
    public boolean updateFee(Long id, FeeDTO feeDTO) {
        Fee existingFee = this.getById(id);
        if (existingFee == null) {
            return false;
        }

        Fee fee = new Fee();
        BeanUtils.copyProperties(feeDTO, fee);
        fee.setId(id);
        fee.setUpdateTime(LocalDateTime.now());

        return this.updateById(fee);
    }

    @Override
    public boolean deleteFee(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean batchDeleteFees(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public boolean updatePaymentStatus(Long id, Integer paymentStatus, BigDecimal paidAmount) {
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        LambdaUpdateWrapper<Fee> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Fee::getId, id)
               .set(Fee::getPaymentStatus, paymentStatus)
               .set(Fee::getPaidAmount, paidAmount)
               .set(Fee::getUpdateTime, LocalDateTime.now());

        if (paymentStatus == 2) { // 已支付
            wrapper.set(Fee::getPaymentTime, LocalDateTime.now());
        }

        return this.update(wrapper);
    }

    @Override
    public Map<String, Object> getFeeStatistics(FeePageDTO pageDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Fee> wrapper = new LambdaQueryWrapper<>();

        // 应用与pageFees相同的查询条件
        if (pageDTO.getFeeType() != null) {
            wrapper.eq(Fee::getFeeType, pageDTO.getFeeType());
        }
        if (StringUtils.hasText(pageDTO.getFeeName())) {
            wrapper.like(Fee::getFeeName, pageDTO.getFeeName());
        }
        if (pageDTO.getStudentId() != null) {
            wrapper.eq(Fee::getStudentId, pageDTO.getStudentId());
        }
        if (StringUtils.hasText(pageDTO.getStudentName())) {
            wrapper.like(Fee::getStudentName, pageDTO.getStudentName());
        }
        if (StringUtils.hasText(pageDTO.getStudentNo())) {
            wrapper.like(Fee::getStudentNo, pageDTO.getStudentNo());
        }
        if (pageDTO.getRoomId() != null) {
            wrapper.eq(Fee::getRoomId, pageDTO.getRoomId());
        }
        if (StringUtils.hasText(pageDTO.getRoomNo())) {
            wrapper.like(Fee::getRoomNo, pageDTO.getRoomNo());
        }
        if (StringUtils.hasText(pageDTO.getBuildingName())) {
            wrapper.like(Fee::getBuildingName, pageDTO.getBuildingName());
        }
        if (pageDTO.getPaymentStatus() != null) {
            wrapper.eq(Fee::getPaymentStatus, pageDTO.getPaymentStatus());
        }
        if (pageDTO.getCollectorId() != null) {
            wrapper.eq(Fee::getCollectorId, pageDTO.getCollectorId());
        }
        if (StringUtils.hasText(pageDTO.getCollectorName())) {
            wrapper.like(Fee::getCollectorName, pageDTO.getCollectorName());
        }
        if (pageDTO.getStartDate() != null) {
            wrapper.ge(Fee::getCreateTime, pageDTO.getStartDate());
        }
        if (pageDTO.getEndDate() != null) {
            wrapper.le(Fee::getCreateTime, pageDTO.getEndDate());
        }

        // 查询所有符合条件的费用
        List<Fee> fees = this.list(wrapper);

        // 统计计算
        Map<String, Object> statistics = new HashMap<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        BigDecimal totalUnpaidAmount = BigDecimal.ZERO;

        int unpaidCount = 0;
        int partiallyPaidCount = 0;
        int fullyPaidCount = 0;

        for (Fee fee : fees) {
            BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
            BigDecimal paidAmount = fee.getPaidAmount() != null ? fee.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal unpaidAmount = amount.subtract(paidAmount);

            totalAmount = totalAmount.add(amount);
            totalPaidAmount = totalPaidAmount.add(paidAmount);
            totalUnpaidAmount = totalUnpaidAmount.add(unpaidAmount);

            Integer status = fee.getPaymentStatus();
            if (status != null) {
                switch (status) {
                    case 0:
                        unpaidCount++;
                        break;
                    case 1:
                        partiallyPaidCount++;
                        break;
                    case 2:
                        fullyPaidCount++;
                        break;
                }
            }
        }

        statistics.put("totalCount", fees.size());
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalPaidAmount", totalPaidAmount);
        statistics.put("totalUnpaidAmount", totalUnpaidAmount);
        statistics.put("unpaidCount", unpaidCount);
        statistics.put("partiallyPaidCount", partiallyPaidCount);
        statistics.put("fullyPaidCount", fullyPaidCount);

        return statistics;
    }

    /**
     * 实体转VO
     */
    private FeeVO convertToVO(Fee fee) {
        FeeVO vo = new FeeVO();
        BeanUtils.copyProperties(fee, vo);

        // 设置枚举值对应的名称
        vo.setFeeTypeName(getFeeTypeName(fee.getFeeType()));
        vo.setBillingCycleName(getBillingCycleName(fee.getBillingCycle()));
        vo.setPaymentStatusName(getPaymentStatusName(fee.getPaymentStatus()));
        vo.setStartTime(fee.getStartTime());
        vo.setEndTime(fee.getEndTime());
        vo.setDueDate(fee.getDueDate());

        // 计算未付金额
        if (fee.getAmount() != null && fee.getPaidAmount() != null) {
            vo.setUnpaidAmount(fee.getAmount().subtract(fee.getPaidAmount()));
        } else if (fee.getAmount() != null) {
            vo.setUnpaidAmount(fee.getAmount());
        }

        return vo;
    }

    /**
     * 获取费用类型名称
     */
    private String getFeeTypeName(Integer feeType) {
        if (feeType == null) return null;
        switch (feeType) {
            case 1: return "住宿费";
            case 2: return "水电费";
            case 3: return "网费";
            case 4: return "其他费用";
            default: return "未知类型";
        }
    }

    /**
     * 获取计费周期名称
     */
    private String getBillingCycleName(Integer billingCycle) {
        if (billingCycle == null) return null;
        switch (billingCycle) {
            case 1: return "按月";
            case 2: return "按学期";
            case 3: return "按年";
            case 4: return "一次性";
            default: return "未知周期";
        }
    }

    /**
     * 获取支付状态名称
     */
    private String getPaymentStatusName(Integer paymentStatus) {
        if (paymentStatus == null) return null;
        switch (paymentStatus) {
            case 0: return "未支付";
            case 1: return "部分支付";
            case 2: return "已支付";
            default: return "未知状态";
        }
    }
}