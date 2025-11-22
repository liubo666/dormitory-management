package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.management.entity.RegistrationApplication;
import com.dormitory.management.entity.SysUser;
import com.dormitory.management.mapper.SysUserMapper;
import com.dormitory.management.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SysUserMapper sysUserMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendRegistrationNotificationToAdmin(RegistrationApplication application) {
        try {
            // 查询所有有效的管理员（role=admin, status=1, deleted=0）
            LambdaQueryWrapper<SysUser> adminWrapper = new LambdaQueryWrapper<>();
            adminWrapper.eq(SysUser::getRole, "admin").eq(SysUser::getStatus, 1) // 启用状态
                    .eq(SysUser::getDeleted, 0) // 未删除
                    .eq(SysUser::getUsername, application.getAdminEmployeeNo())
                    .isNotNull(SysUser::getEmail); // 有邮箱地址
            SysUser sysUser = sysUserMapper.selectOne(adminWrapper);
            if (Objects.isNull(sysUser)|| StringUtils.isBlank(sysUser.getEmail())) {
                log.warn("没有找到有效的管理员邮箱，无法发送通知邮件");
                return;
            }

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 发件人：系统邮箱（固定）
            helper.setFrom(new InternetAddress(fromEmail, "宿舍管理系统"));
            // 收件人：目标人邮箱（支持多个）
            helper.setTo(sysUser.getEmail());
            // 邮件标题
            helper.setSubject("新的注册申请 - " + application.getApplicationNo());


            // 邮件内容（HTML 格式，包含发起人所有信息）
            String htmlContent = buildAdminNotificationEmail(application);
            helper.setText(htmlContent, true);  // true 表示 HTML 格式

            // 发送邮件
            mailSender.send(mimeMessage);
//            log.info("管理员通知邮件发送成功，申请编号：{}，发送给{}个管理员",
//                    application.getApplicationNo(), adminEmails.length);
        } catch (Exception e) {
            log.error("发送管理员通知邮件失败，申请编号：{}", application.getApplicationNo(), e);
            throw new RuntimeException("邮件发送失败");
        }
    }

    @Override
    public void sendApprovalEmailToUser(RegistrationApplication application, boolean approved, String rejectionReason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(application.getEmail());

            if (approved) {
                helper.setSubject("注册申请已通过 - 宿舍管理系统");
                String content = buildApprovalEmail(application);
                helper.setText(content, true);
            } else {
                helper.setSubject("注册申请已驳回 - 宿舍管理系统");
                String content = buildRejectionEmail(application, rejectionReason);
                helper.setText(content, true);
            }

            mailSender.send(message);
            log.info("审批结果邮件发送成功，用户邮箱：{}，审批结果：{}", application.getEmail(), approved ? "通过" : "驳回");
        } catch (Exception e) {
            log.error("发送审批结果邮件失败，用户邮箱：{}", application.getEmail(), e);
            throw new RuntimeException("邮件发送失败");
        }
    }

    @Override
    public void sendTestEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("测试邮件发送成功，收件人：{}", to);
        } catch (Exception e) {
            log.error("测试邮件发送失败，收件人：{}", to, e);
            throw new RuntimeException("邮件发送失败");
        }
    }

    /**
     * 构建管理员通知邮件内容
     */
    private String buildAdminNotificationEmail(RegistrationApplication application) {
        // 智能跳转机制
        String approveUrl = frontendUrl + "/registration/admin/approve/" + application.getApprovalToken() + "?action=approve";
        String rejectUrl = frontendUrl + "/registration/admin/approve/" + application.getApprovalToken() + "?action=reject";

        StringWriter writer = new StringWriter();
        writer.write("<!DOCTYPE html>");
        writer.write("<html><head><meta charset='UTF-8'><title>宿舍管理系统 - 新的注册申请</title></head><body>");

        // 外层容器 - 符合项目风格
        writer.write("<div style='font-family: \"Microsoft YaHei\", \"PingFang SC\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; max-width: 600px; margin: 20px auto; background: linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(30, 64, 175, 0.1);'>");

        // 头部 - 学术蓝渐变背景
        writer.write("<div style='background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); padding: 30px; text-align: center;'>");
        writer.write("<div style='color: white;'>");
        writer.write("<h1 style='margin: 0; font-size: 28px; font-weight: 600; letter-spacing: 0.5px;'>🏫 宿舍管理系统</h1>");
        writer.write("<p style='margin: 10px 0 0 0; font-size: 16px; opacity: 0.9; letter-spacing: 0.3px;'>新的注册申请通知</p>");
        writer.write("</div>");
        writer.write("</div>");

        // 主体内容
        writer.write("<div style='padding: 30px; background: white;'>");
        writer.write("<p style='font-size: 16px; color: #1f2937; margin: 0 0 20px 0; line-height: 1.6;'>尊敬的管理员，您好！</p>");
        writer.write("<p style='font-size: 15px; color: #4b5563; margin: 0 0 25px 0; line-height: 1.6;'>系统收到新的宿舍注册申请，请及时审核处理：</p>");

        // 申请信息表格 - 现代化设计
        writer.write("<div style='background: #f8fafc; border-radius: 12px; padding: 20px; margin-bottom: 30px; border-left: 4px solid #1e40af;'>");
        writer.write("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 15px;'>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>申请编号</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getApplicationNo() + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>申请时间</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getCreateTime() + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>用户名</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getUsername() + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>姓名</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getRealName() + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>性别</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + (application.getGender() == 1 ? "男" : "女") + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>手机号</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getPhone() + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>邮箱</span><br><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getEmail() + "</span></div>");
        writer.write("<div style='margin-bottom: 12px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500;'>状态</span><br><span style='color: #d97706; font-size: 14px; font-weight: 600; background: #fef3c7; padding: 4px 12px; border-radius: 20px; display: inline-block;'>待审批</span></div>");
        writer.write("</div>");
        writer.write("</div>");

        // 审批操作区域 - 项目风格
        writer.write("<div style='background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%); border-radius: 12px; padding: 25px; margin-bottom: 20px; border: 1px solid #bae6fd;'>");
        writer.write("<h3 style='margin: 0 0 20px 0; color: #1e40af; font-size: 18px; font-weight: 600; text-align: center;'>⚡ 快速审批操作</h3>");

        // 审批按钮 - 使用项目主题色
        writer.write("<div style='text-align: center; margin-bottom: 25px;'>");
        writer.write("<a href='" + approveUrl + "' target='_blank' style='display: inline-block; background: linear-gradient(135deg, #059669 0%, #10b981 100%); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; margin: 0 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3); transition: all 0.3s ease;'>✓ 通过申请</a>");
        writer.write("<a href='" + rejectUrl + "' target='_blank' style='display: inline-block; background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; margin: 0 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3); transition: all 0.3s ease;'>✗ 驳回申请</a>");
        writer.write("</div>");

        // 备用链接区域
        writer.write("<div style='background: white; padding: 20px; border-radius: 8px; border: 1px solid #e5e7eb;'>");
        writer.write("<p style='margin: 0 0 15px 0; color: #6b7280; font-size: 13px; text-align: center;'>🔗 如果按钮无法点击，请复制以下链接到浏览器地址栏</p>");
        writer.write("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 15px;'>");
        writer.write("<div>");
        writer.write("<p style='margin: 0 0 8px 0; color: #059669; font-weight: 600; font-size: 13px;'>✓ 通过链接：</p>");
        writer.write("<p style='margin: 0; word-break: break-all; font-size: 11px; color: #4b5563; background: #f8fafc; padding: 8px; border-radius: 4px; border: 1px solid #e5e7eb; font-family: \"Consolas\", \"Monaco\", monospace;'>" + approveUrl + "</p>");
        writer.write("</div>");
        writer.write("<div>");
        writer.write("<p style='margin: 0 0 8px 0; color: #dc2626; font-weight: 600; font-size: 13px;'>✗ 驳回链接：</p>");
        writer.write("<p style='margin: 0; word-break: break-all; font-size: 11px; color: #4b5563; background: #f8fafc; padding: 8px; border-radius: 4px; border: 1px solid #e5e7eb; font-family: \"Consolas\", \"Monaco\", monospace;'>" + rejectUrl + "</p>");
        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");

        writer.write("</div>");

        // 底部 - 项目风格
        writer.write("<div style='background: #f8fafc; padding: 20px; text-align: center; border-top: 1px solid #e5e7eb;'>");
        writer.write("<p style='margin: 0 0 10px 0; color: #6b7280; font-size: 12px; line-height: 1.5;'>此邮件由宿舍管理系统自动发送，请勿回复</p>");
        writer.write("<p style='margin: 0; color: #9ca3af; font-size: 11px;'>审批链接7天内有效 | 技术支持：宿舍管理团队</p>");
        writer.write("</div>");

        writer.write("</div></body></html>");

        return writer.toString();
    }

  
    /**
     * 构建通过邮件内容
     */
    private String buildApprovalEmail(RegistrationApplication application) {
        StringWriter writer = new StringWriter();
        writer.write("<!DOCTYPE html>");
        writer.write("<html><head><meta charset='UTF-8'><title>宿舍管理系统 - 注册申请已通过</title></head><body>");

        // 外层容器 - 符合项目风格
        writer.write("<div style='font-family: \"Microsoft YaHei\", \"PingFang SC\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; max-width: 600px; margin: 20px auto; background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(5, 150, 105, 0.1);'>");

        // 头部 - 成功绿色渐变背景
        writer.write("<div style='background: linear-gradient(135deg, #059669 0%, #10b981 100%); padding: 30px; text-align: center;'>");
        writer.write("<div style='color: white;'>");
        writer.write("<div style='font-size: 48px; margin-bottom: 15px;'>🎉</div>");
        writer.write("<h1 style='margin: 0; font-size: 28px; font-weight: 600; letter-spacing: 0.5px;'>欢迎加入宿舍管理系统</h1>");
        writer.write("<p style='margin: 10px 0 0 0; font-size: 16px; opacity: 0.9; letter-spacing: 0.3px;'>您的注册申请已通过审核</p>");
        writer.write("</div>");
        writer.write("</div>");

        // 主体内容
        writer.write("<div style='padding: 30px; background: white;'>");
        writer.write("<p style='font-size: 16px; color: #1f2937; margin: 0 0 20px 0; line-height: 1.6;'>尊敬的 <strong style='color: #059669;'>" + application.getRealName() + "</strong>，您好！</p>");
        writer.write("<p style='font-size: 15px; color: #4b5563; margin: 0 0 25px 0; line-height: 1.6;'>恭喜您！您的宿舍管理系统注册申请已通过审核，现在可以使用以下信息登录系统：</p>");

        // 登录信息卡片
        writer.write("<div style='background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%); border-radius: 12px; padding: 25px; margin-bottom: 30px; border-left: 4px solid #059669;'>");
        writer.write("<h3 style='margin: 0 0 20px 0; color: #059669; font-size: 16px; font-weight: 600; text-align: center;'>🔑 登录信息</h3>");
        writer.write("<div style='background: white; border-radius: 8px; padding: 20px; border: 1px solid #d1fae5;'>");
        writer.write("<div style='margin-bottom: 15px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500; display: block; margin-bottom: 5px;'>用户名</span><span style='color: #1f2937; font-size: 16px; font-weight: 600; font-family: \"Consolas\", \"Monaco\", monospace; background: #f8fafc; padding: 8px 12px; border-radius: 4px; display: inline-block;'>" + application.getUsername() + "</span></div>");
        writer.write("<div style='margin-bottom: 15px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500; display: block; margin-bottom: 5px;'>密码</span><span style='color: #1f2937; font-size: 14px; font-weight: 500;'>您注册时设置的密码</span></div>");
        writer.write("<div><span style='color: #6b7280; font-size: 13px; font-weight: 500; display: block; margin-bottom: 5px;'>登录地址</span><a href='" + frontendUrl + "' target='_blank' style='color: #059669; text-decoration: none; font-weight: 600; font-size: 14px; display: inline-flex; align-items: center;'><span style='margin-right: 5px;'>🌐</span>" + frontendUrl + "</a></div>");
        writer.write("</div>");
        writer.write("</div>");

        // 温馨提示
        writer.write("<div style='background: #fefce8; border-radius: 12px; padding: 20px; margin-bottom: 20px; border: 1px solid #fde047;'>");
        writer.write("<h3 style='margin: 0 0 15px 0; color: #d97706; font-size: 16px; font-weight: 600; display: flex; align-items: center;'><span style='margin-right: 8px;'>💡</span>温馨提示</h3>");
        writer.write("<ul style='margin: 0; padding-left: 20px; color: #4b5563; font-size: 14px; line-height: 1.8;'>");
        writer.write("<li style='margin-bottom: 8px; color: #059669; font-weight: 500;'>✓ 请使用您注册时设置的密码登录系统</li>");
        writer.write("<li style='margin-bottom: 8px; color: #059669; font-weight: 500;'>✓ 首次登录后建议完善个人信息，以便更好的使用系统功能</li>");
        writer.write("<li style='margin-bottom: 8px; color: #059669; font-weight: 500;'>✓ 如在登录过程中遇到问题，请及时联系管理员</li>");
        writer.write("<li style='color: #d97706; font-weight: 500;'>⚠️ 请妥善保管您的登录信息，不要与他人分享</li>");
        writer.write("</ul>");
        writer.write("</div>");

        // 快速登录按钮
        writer.write("<div style='text-align: center; margin-bottom: 20px;'>");
        writer.write("<a href='" + frontendUrl + "' target='_blank' style='display: inline-block; background: linear-gradient(135deg, #059669 0%, #10b981 100%); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3); transition: all 0.3s ease; letter-spacing: 0.5px;'>🚀 立即登录</a>");
        writer.write("</div>");

        writer.write("</div>");

        // 底部
        writer.write("<div style='background: #f0fdf4; padding: 20px; text-align: center; border-top: 1px solid #d1fae5;'>");
        writer.write("<p style='margin: 0 0 10px 0; color: #6b7280; font-size: 12px; line-height: 1.5;'>此邮件由宿舍管理系统自动发送，请勿回复</p>");
        writer.write("<p style='margin: 0; color: #9ca3af; font-size: 11px;'>期待您的使用，祝您生活愉快！</p>");
        writer.write("</div>");

        writer.write("</div></body></html>");

        return writer.toString();
    }

    /**
     * 构建驳回邮件内容
     */
    private String buildRejectionEmail(RegistrationApplication application, String rejectionReason) {
        StringWriter writer = new StringWriter();
        writer.write("<!DOCTYPE html>");
        writer.write("<html><head><meta charset='UTF-8'><title>宿舍管理系统 - 注册申请已驳回</title></head><body>");

        // 外层容器 - 符合项目风格
        writer.write("<div style='font-family: \"Microsoft YaHei\", \"PingFang SC\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; max-width: 600px; margin: 20px auto; background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(220, 38, 38, 0.1);'>");

        // 头部 - 危险红色渐变背景
        writer.write("<div style='background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%); padding: 30px; text-align: center;'>");
        writer.write("<div style='color: white;'>");
        writer.write("<div style='font-size: 48px; margin-bottom: 15px;'>😔</div>");
        writer.write("<h1 style='margin: 0; font-size: 28px; font-weight: 600; letter-spacing: 0.5px;'>注册申请审核结果</h1>");
        writer.write("<p style='margin: 10px 0 0 0; font-size: 16px; opacity: 0.9; letter-spacing: 0.3px;'>您的注册申请已被驳回</p>");
        writer.write("</div>");
        writer.write("</div>");

        // 主体内容
        writer.write("<div style='padding: 30px; background: white;'>");
        writer.write("<p style='font-size: 16px; color: #1f2937; margin: 0 0 20px 0; line-height: 1.6;'>尊敬的 <strong style='color: #dc2626;'>" + application.getRealName() + "</strong>，您好！</p>");
        writer.write("<p style='font-size: 15px; color: #4b5563; margin: 0 0 25px 0; line-height: 1.6;'>很抱歉地通知您，您的宿舍管理系统注册申请已被管理员驳回。具体情况如下：</p>");

        // 驳回详情卡片
        writer.write("<div style='background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%); border-radius: 12px; padding: 25px; margin-bottom: 30px; border-left: 4px solid #dc2626;'>");
        writer.write("<h3 style='margin: 0 0 20px 0; color: #dc2626; font-size: 16px; font-weight: 600; text-align: center;'>📋 驳回详情</h3>");
        writer.write("<div style='background: white; border-radius: 8px; padding: 20px; border: 1px solid #fecaca;'>");
        writer.write("<div style='margin-bottom: 15px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500; display: block; margin-bottom: 5px;'>申请编号</span><span style='color: #1f2937; font-size: 15px; font-weight: 600; font-family: \"Consolas\", \"Monaco\", monospace; background: #f8fafc; padding: 8px 12px; border-radius: 4px; display: inline-block;'>" + application.getApplicationNo() + "</span></div>");
        writer.write("<div style='margin-bottom: 15px;'><span style='color: #6b7280; font-size: 13px; font-weight: 500; display: block; margin-bottom: 5px;'>驳回时间</span><span style='color: #1f2937; font-size: 15px; font-weight: 600;'>" + application.getApprovedTime() + "</span></div>");
        writer.write("<div><span style='color: #6b7280; font-size: 13px; font-weight: 500; display: block; margin-bottom: 5px;'>驳回原因</span><span style='color: #dc2626; font-size: 14px; font-weight: 500; background: #fef2f2; padding: 10px 12px; border-radius: 4px; display: inline-block; border: 1px solid #fecaca;'>" + (rejectionReason != null ? rejectionReason : "未提供具体原因") + "</span></div>");
        writer.write("</div>");
        writer.write("</div>");

        // 后续建议
        writer.write("<div style='background: #fff7ed; border-radius: 12px; padding: 20px; margin-bottom: 20px; border: 1px solid #fed7aa;'>");
        writer.write("<h3 style='margin: 0 0 15px 0; color: #d97706; font-size: 16px; font-weight: 600; display: flex; align-items: center;'><span style='margin-right: 8px;'>📝</span>后续建议</h3>");
        writer.write("<ul style='margin: 0; padding-left: 20px; color: #4b5563; font-size: 14px; line-height: 1.8;'>");
        writer.write("<li style='margin-bottom: 8px; color: #dc2626; font-weight: 500;'>🔄 请根据驳回原因修改相关信息后重新提交申请</li>");
        writer.write("<li style='margin-bottom: 8px; color: #1e40af; font-weight: 500;'>📞 如对驳回结果有疑问，可直接联系管理员了解详细情况</li>");
        writer.write("<li style='color: #d97706; font-weight: 500;'>💡 建议仔细阅读注册要求，确保提交信息完整准确</li>");
        writer.write("</ul>");
        writer.write("</div>");

        // 重新申请按钮
        writer.write("<div style='text-align: center; margin-bottom: 20px;'>");
        writer.write("<a href='" + frontendUrl + "/register' target='_blank' style='display: inline-block; background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3); transition: all 0.3s ease; letter-spacing: 0.5px;'>🔄 重新申请</a>");
        writer.write("</div>");

        writer.write("</div>");

        // 底部
        writer.write("<div style='background: #fef2f2; padding: 20px; text-align: center; border-top: 1px solid #fecaca;'>");
        writer.write("<p style='margin: 0 0 10px 0; color: #6b7280; font-size: 12px; line-height: 1.5;'>此邮件由宿舍管理系统自动发送，请勿回复</p>");
        writer.write("<p style='margin: 0; color: #9ca3af; font-size: 11px;'>感谢您的理解与支持，期待您的再次申请！</p>");
        writer.write("</div>");

        writer.write("</div></body></html>");

        return writer.toString();
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "宿舍管理系统"));
            helper.setTo(to);
            helper.setSubject("密码重置请求 - 宿舍管理系统");

            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String content = buildPasswordResetEmail(resetUrl);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("密码重置邮件发送成功，用户邮箱：{}", to);
        } catch (Exception e) {
            log.error("发送密码重置邮件失败，用户邮箱：{}", to, e);
            throw new RuntimeException("邮件发送失败");
        }
    }

    /**
     * 构建密码重置邮件内容
     */
    private String buildPasswordResetEmail(String resetUrl) {
        StringWriter writer = new StringWriter();
        writer.write("<!DOCTYPE html>");
        writer.write("<html><head><meta charset='UTF-8'><title>宿舍管理系统 - 密码重置</title></head><body>");

        // 外层容器 - 符合项目风格
        writer.write("<div style='font-family: \"Microsoft YaHei\", \"PingFang SC\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; max-width: 600px; margin: 20px auto; background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%); border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(30, 64, 175, 0.1);'>");

        // 头部 - 学术蓝渐变背景
        writer.write("<div style='background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); padding: 30px; text-align: center;'>");
        writer.write("<div style='color: white;'>");
        writer.write("<div style='font-size: 48px; margin-bottom: 15px;'>🔑</div>");
        writer.write("<h1 style='margin: 0; font-size: 28px; font-weight: 600; letter-spacing: 0.5px;'>密码重置请求</h1>");
        writer.write("<p style='margin: 10px 0 0 0; font-size: 16px; opacity: 0.9; letter-spacing: 0.3px;'>宿舍管理系统</p>");
        writer.write("</div>");
        writer.write("</div>");

        // 主体内容
        writer.write("<div style='padding: 30px; background: white;'>");
        writer.write("<p style='font-size: 16px; color: #1f2937; margin: 0 0 20px 0; line-height: 1.6;'>您好！</p>");
        writer.write("<p style='font-size: 15px; color: #4b5563; margin: 0 0 25px 0; line-height: 1.6;'>我们收到了您的密码重置请求。请点击下面的按钮重置您的密码：</p>");

        // 重置按钮区域
        writer.write("<div style='background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%); border-radius: 12px; padding: 25px; margin-bottom: 20px; border: 1px solid #bae6fd;'>");
        writer.write("<h3 style='margin: 0 0 20px 0; color: #1e40af; font-size: 18px; font-weight: 600; text-align: center;'>🔐 密码重置操作</h3>");

        // 重置按钮
        writer.write("<div style='text-align: center; margin-bottom: 25px;'>");
        writer.write("<a href='" + resetUrl + "' target='_blank' style='display: inline-block; background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; margin: 0 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3); transition: all 0.3s ease;'>🔗 重置密码</a>");
        writer.write("</div>");

        // 备用链接区域
        writer.write("<div style='background: white; padding: 20px; border-radius: 8px; border: 1px solid #e5e7eb;'>");
        writer.write("<p style='margin: 0 0 15px 0; color: #6b7280; font-size: 13px; text-align: center;'>🔗 如果按钮无法点击，请复制以下链接到浏览器地址栏</p>");
        writer.write("<p style='margin: 0; word-break: break-all; font-size: 11px; color: #4b5563; background: #f8fafc; padding: 8px; border-radius: 4px; border: 1px solid #e5e7eb; font-family: \"Consolas\", \"Monaco\", monospace;'>" + resetUrl + "</p>");
        writer.write("</div>");
        writer.write("</div>");

        // 安全提醒
        writer.write("<div style='background: #fefce8; border-radius: 12px; padding: 20px; margin-bottom: 20px; border: 1px solid #fde047;'>");
        writer.write("<h3 style='margin: 0 0 15px 0; color: #d97706; font-size: 16px; font-weight: 600; display: flex; align-items: center;'><span style='margin-right: 8px;'>🛡️</span>安全提醒</h3>");
        writer.write("<ul style='margin: 0; padding-left: 20px; color: #4b5563; font-size: 14px; line-height: 1.8;'>");
        writer.write("<li style='margin-bottom: 8px; color: #1e40af; font-weight: 500;'>• 此重置链接仅在 <strong>24小时</strong> 内有效</li>");
        writer.write("<li style='margin-bottom: 8px; color: #1e40af; font-weight: 500;'>• 如果您没有请求重置密码，请忽略此邮件</li>");
        writer.write("<li style='margin-bottom: 8px; color: #1e40af; font-weight: 500;'>• 为了账户安全，请不要将此链接分享给他人</li>");
        writer.write("<li style='color: #d97706; font-weight: 500;'>⚠️ 如有疑问，请及时联系管理员</li>");
        writer.write("</ul>");
        writer.write("</div>");

        writer.write("</div>");

        // 底部
        writer.write("<div style='background: #f0f9ff; padding: 20px; text-align: center; border-top: 1px solid #bae6fd;'>");
        writer.write("<p style='margin: 0 0 10px 0; color: #6b7280; font-size: 12px; line-height: 1.5;'>此邮件由宿舍管理系统自动发送，请勿回复</p>");
        writer.write("<p style='margin: 0; color: #9ca3af; font-size: 11px;'>重置链接24小时内有效 | 技术支持：宿舍管理团队</p>");
        writer.write("</div>");

        writer.write("</div></body></html>");

        return writer.toString();
    }
}