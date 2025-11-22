package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.dto.RegistrationApplicationDTO;
import com.dormitory.management.entity.RegistrationApplication;
import com.dormitory.management.entity.SysUser;
import com.dormitory.management.entity.UserInfo;
import com.dormitory.management.mapper.RegistrationApplicationMapper;
import com.dormitory.management.mapper.SysUserMapper;
import com.dormitory.management.service.RegistrationApplicationService;
import com.dormitory.management.service.EmailService;
import com.dormitory.management.utils.PasswordUtil;
import com.dormitory.management.vo.RegistrationApplicationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 注册申请服务实现类
 */
@Service
@RequiredArgsConstructor
public class RegistrationApplicationServiceImpl implements RegistrationApplicationService {

    private final RegistrationApplicationMapper registrationApplicationMapper;
    private final SysUserMapper sysUserMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public String submitApplication(RegistrationApplicationDTO dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<RegistrationApplication> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(RegistrationApplication::getUsername, dto.getUsername());
        if (registrationApplicationMapper.selectOne(usernameWrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        LambdaQueryWrapper<RegistrationApplication> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(RegistrationApplication::getEmail, dto.getEmail());
        if (registrationApplicationMapper.selectOne(emailWrapper) != null) {
            throw new RuntimeException("该邮箱已提交过注册申请");
        }

        // 验证管理员工号
        if (!validateAdminEmployeeNo(dto.getAdminEmployeeNo())) {
            throw new RuntimeException("管理员工号无效");
        }

        // 检查密码确认
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 创建注册申请
        RegistrationApplication application = new RegistrationApplication();
        application.setApplicationNo(generateApplicationNo());
        application.setUsername(dto.getUsername());
        application.setPassword(PasswordUtil.encode(dto.getPassword()));
        application.setRealName(dto.getRealName());
        application.setGender(dto.getGender());
        application.setPhone(dto.getPhone());
        application.setEmail(dto.getEmail());
        application.setAdminEmployeeNo(dto.getAdminEmployeeNo());
        application.setStatus(0); // 待审批
        application.setApprovalToken(UUID.randomUUID().toString());
        application.setTokenExpireTime(LocalDateTime.now().plusDays(7)); // 7天后过期

        registrationApplicationMapper.insert(application);

        // 发送邮件给管理员
        try {
            emailService.sendRegistrationNotificationToAdmin(application);
        } catch (Exception e) {
            // 邮件发送失败不影响注册流程
            System.err.println("发送管理员通知邮件失败: " + e.getMessage());
        }

        return application.getApplicationNo();
    }

    @Override
    public IPage<RegistrationApplicationVO> getApplicationPage(Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<RegistrationApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(RegistrationApplication::getStatus, status);
        }
        wrapper.orderByDesc(RegistrationApplication::getCreateTime);
        Page<RegistrationApplication> page = new Page<>(1, 10);
        // 执行分页查询
        IPage<RegistrationApplication> applicationPage = registrationApplicationMapper.selectPage(page, wrapper);

        // 转换为VO
        IPage<RegistrationApplicationVO> voPage = new Page<>();
        voPage.setCurrent(applicationPage.getCurrent());
        voPage.setSize(applicationPage.getSize());
        voPage.setTotal(applicationPage.getTotal());

        List<RegistrationApplicationVO> voList = applicationPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }





    @Override
    @Transactional
    public void approveApplication(String approvalToken) {
        // 根据token查询申请
        LambdaQueryWrapper<RegistrationApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegistrationApplication::getApprovalToken, approvalToken);
        RegistrationApplication application = registrationApplicationMapper.selectOne(wrapper);

        if (application == null) {
            throw new RuntimeException("无效的审批链接");
        }

        if (application.getTokenExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("审批链接已过期");
        }

        if (application.getStatus() != 0) {
            throw new RuntimeException("该申请已被处理");
        }

        UserInfo currentUser = UserContext.getCurrentUser();
        // 验证管理员权限：检查当前用户是否为指定的管理员
        LambdaQueryWrapper<SysUser> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.eq(SysUser::getUsername, currentUser.getUsername())
                  .eq(SysUser::getRole, "admin")
                  .eq(SysUser::getStatus, 1) // 启用状态
                  .eq(SysUser::getDeleted, 0); // 未删除
        SysUser admin = sysUserMapper.selectOne(adminWrapper);

        if (admin == null) {
            throw new RuntimeException("无权限进行审批操作");
        }

        // 检查是否为指定的管理员（通过管理员工号）
        if (!admin.getUsername().equals(application.getAdminEmployeeNo())) {
            throw new RuntimeException("您无权处理此申请，只有指定的管理员才能审批");
        }

        // 更新申请状态
        application.setStatus(1); // 已通过
        application.setApprovedBy(admin.getId());
        application.setApprovedTime(LocalDateTime.now());
        registrationApplicationMapper.updateById(application);

        // 创建正式用户账号
        SysUser user = new SysUser();
        user.setUsername(application.getUsername());
        user.setPassword(application.getPassword());
        user.setName(application.getRealName());
        user.setGender(application.getGender());
        user.setPhone(application.getPhone());
        user.setEmail(application.getEmail());
        user.setRole("user");
        user.setStatus(1); // 启用
        sysUserMapper.insert(user);

        // 发送通过邮件给用户
        try {
            emailService.sendApprovalEmailToUser(application, true, null);
        } catch (Exception e) {
            System.err.println("发送通过邮件失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void rejectApplication(String approvalToken, String rejectionReason) {
        // 根据token查询申请
        LambdaQueryWrapper<RegistrationApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegistrationApplication::getApprovalToken, approvalToken);
        RegistrationApplication application = registrationApplicationMapper.selectOne(wrapper);

        if (application == null) {
            throw new RuntimeException("无效的审批链接");
        }

        if (application.getTokenExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("审批链接已过期");
        }

        if (application.getStatus() != 0) {
            throw new RuntimeException("该申请已被处理");
        }
        UserInfo currentUser = UserContext.getCurrentUser();
        // 验证管理员权限：检查当前用户是否为指定的管理员
        LambdaQueryWrapper<SysUser> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.eq(SysUser::getUsername, currentUser.getUsername())
                  .eq(SysUser::getRole, "admin")
                  .eq(SysUser::getStatus, 1) // 启用状态
                  .eq(SysUser::getDeleted, 0); // 未删除
        SysUser admin = sysUserMapper.selectOne(adminWrapper);

        if (admin == null) {
            throw new RuntimeException("无权限进行审批操作");
        }

        // 检查是否为指定的管理员（通过管理员工号）
        if (!admin.getUsername().equals(application.getAdminEmployeeNo())) {
            throw new RuntimeException("您无权处理此申请，只有指定的管理员才能审批");
        }

        // 更新申请状态
        application.setStatus(2); // 已驳回
        application.setApprovedBy(admin.getId());
        application.setApprovedTime(LocalDateTime.now());
        application.setRejectionReason(rejectionReason);
        registrationApplicationMapper.updateById(application);

        // 发送驳回邮件给用户
        try {
            emailService.sendApprovalEmailToUser(application, false, rejectionReason);
        } catch (Exception e) {
            System.err.println("发送驳回邮件失败: " + e.getMessage());
        }
    }

    @Override
    public RegistrationApplicationVO getApplicationByToken(String approvalToken) {
        LambdaQueryWrapper<RegistrationApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegistrationApplication::getApprovalToken, approvalToken);
        RegistrationApplication application = registrationApplicationMapper.selectOne(wrapper);

        if (application == null) {
            return null;
        }

        return convertToVO(application);
    }

    @Override
    public boolean validateAdminEmployeeNo(String employeeNo) {
        // 验证管理员工号：检查是否存在对应的admin用户
        // 条件：username = 填写的工号, role = "admin", status = 1, deleted = 0
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, employeeNo)
               .eq(SysUser::getRole, "admin")
               .eq(SysUser::getStatus, 1) // 启用状态
               .eq(SysUser::getDeleted, 0); // 未删除

        SysUser admin = sysUserMapper.selectOne(wrapper);
        return admin != null;
    }

    /**
     * 转换为VO对象
     */
    private RegistrationApplicationVO convertToVO(RegistrationApplication application) {
        RegistrationApplicationVO vo = new RegistrationApplicationVO();
        vo.setId(application.getId());
        vo.setApplicationNo(application.getApplicationNo());
        vo.setUsername(application.getUsername());
        vo.setRealName(application.getRealName());
        vo.setGender(application.getGender());
        vo.setPhone(application.getPhone());
        vo.setEmail(application.getEmail());
        vo.setAdminEmployeeNo(application.getAdminEmployeeNo());
        vo.setStatus(application.getStatus());
        vo.setStatusText(getStatusText(application.getStatus()));
        vo.setRejectionReason(application.getRejectionReason());
        vo.setCreateTime(application.getCreateTime());
        vo.setApprovedTime(application.getApprovedTime());

        if (application.getApprovedBy() != null) {
            SysUser admin = sysUserMapper.selectById(application.getApprovedBy());
            if (admin != null) {
                vo.setApprovedByName(admin.getName());
            }
        }

        return vo;
    }

    /**
     * 生成申请编号
     */
    private String generateApplicationNo() {
        return "REG" + System.currentTimeMillis();
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待审批";
            case 1: return "已通过";
            case 2: return "已驳回";
            default: return "未知";
        }
    }
}