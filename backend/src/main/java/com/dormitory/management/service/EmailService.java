package com.dormitory.management.service;

import com.dormitory.management.entity.RegistrationApplication;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送注册申请通知邮件给管理员
     */
    void sendRegistrationNotificationToAdmin(RegistrationApplication application);

    /**
     * 发送审批结果邮件给用户
     */
    void sendApprovalEmailToUser(RegistrationApplication application, boolean approved, String rejectionReason);

//    /**
//     * 发送测试邮件
//     */
//    void sendTestEmail(String to, String subject, String content);

    /**
     * 发送密码重置邮件
     */
    void sendPasswordResetEmail(String to, String resetToken);

    /**
     * 发送密码重置成功通知邮件
     */
    void sendPasswordResetSuccessEmail(String to, String username);
}