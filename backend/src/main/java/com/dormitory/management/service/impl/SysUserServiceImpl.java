package com.dormitory.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.management.dto.LoginDTO;
import com.dormitory.management.dto.LoginVO;
import com.dormitory.management.entity.SysUser;
import com.dormitory.management.entity.UserInfo;
import com.dormitory.management.mapper.SysUserMapper;
import com.dormitory.management.service.SysUserService;
import com.dormitory.management.util.JwtUtil;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.utils.PasswordUtil;
import com.dormitory.management.vo.UserInfoVO;
import com.dormitory.management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // 简单的令牌存储机制（实际项目中应该使用Redis数据库）
    private static final ConcurrentHashMap<String, Long> resetTokens = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> tokenExpiry = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRY_HOURS = 24; // 令牌24小时有效

    @Override
    public LoginVO login(LoginDTO loginDTO) {

        // 根据用户名查询用户
        SysUser user = getUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT Token
        String token = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 获取完整用户信息并设置到上下文（为登录后的第一个请求准备）
        try {
            UserInfo userInfo = getUserInfoFromDb(user.getId());
            if (userInfo != null) {
                UserContext.setCurrentUser(userInfo);
                log.info("用户登录成功，设置用户上下文: {}", userInfo.getUsername());
            }
        } catch (Exception e) {
            log.warn("登录后设置用户上下文失败，但登录仍成功: {}", e.getMessage());
        }

        // 返回登录结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);

        return loginVO;
    }

    @Override
    public void logout() {
        // JWT是无状态的，客户端删除token即可
        // 如果需要服务端控制，可以添加token黑名单机制
        // Spring Security会自动清理SecurityContext
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        UserInfo userInfo = com.dormitory.management.context.UserContext.getCurrentUser();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(userInfo.getUserId());
        userInfoVO.setUsername(userInfo.getUsername());
        userInfoVO.setName(userInfo.getRealName());
        userInfoVO.setRole(userInfo.getRoleId());
        userInfoVO.setEnabled(userInfo.getEnabled());

        return userInfoVO;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    public UserInfo getUserInfoFromDb(Long userId) {
        SysUser user = getById(userId);
        if (user == null || user.getStatus() != 1) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getName());
        userInfo.setRoleId(user.getRole());
        userInfo.setRoleName(getRoleName(user.getRole()));
        userInfo.setEnabled(user.getStatus() == 1);

        return userInfo;
    }

    private String getRoleName(String roleId) {
        if ("admin".equals(roleId)) {
            return "管理员";
        } else if ("user".equals(roleId)) {
            return "普通用户";
        }
        return "其他";
    }

    @Override
    public void sendResetPasswordEmail(String username, String email) {
        // 根据用户名和邮箱查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getEmail, email)
               .eq(SysUser::getStatus, 1)
               .eq(SysUser::getDeleted, 0);

        SysUser user = getOne(wrapper);
        if (user == null) {
            log.warn("未找到用户名和邮箱对应的用户：{}, {}", username, email);
            // 为了安全，不泄露用户是否存在的信息
            // 可以抛出异常或返回成功，但不暴露具体信息
            // 这里选择抛出异常，因为前端需要显示错误信息
            throw new RuntimeException("用户名和邮箱不匹配或该用户已被禁用");
        }

        try {
            // 生成重置令牌
            String resetToken = UUID.randomUUID().toString().replace("-", "");

            // 存储重置令牌和用户ID关联，并设置过期时间
            resetTokens.put(resetToken, user.getId());
            tokenExpiry.put(resetToken, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(TOKEN_EXPIRY_HOURS));

            // 发送密码重置邮件
            emailService.sendPasswordResetEmail(email, resetToken);

            log.info("密码重置邮件发送成功，邮箱：{}，用户：{}，令牌：{}", email, user.getUsername(), resetToken);
        } catch (Exception e) {
            log.error("发送密码重置邮件失败，邮箱：{}，用户：{}", email, username, e);
            throw new RuntimeException("发送密码重置邮件失败：" + e.getMessage());
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // 验证令牌是否存在
        if (!resetTokens.containsKey(token)) {
            throw new RuntimeException("重置令牌无效或已过期");
        }

        // 验证令牌是否过期
        Long expiryTime = tokenExpiry.get(token);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            // 清理过期令牌
            resetTokens.remove(token);
            tokenExpiry.remove(token);
            throw new RuntimeException("重置令牌无效或已过期");
        }

        // 获取用户ID
        Long userId = resetTokens.get(token);
        if (userId == null) {
            throw new RuntimeException("重置令牌无效");
        }

        try {
            // 查询用户
            SysUser user = getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                throw new RuntimeException("用户已被禁用");
            }

            // 更新密码
            String encryptedPassword = PasswordUtil.encode(newPassword);
            user.setPassword(encryptedPassword);

            // 清理重置令牌
            resetTokens.remove(token);
            tokenExpiry.remove(token);

            // 保存更新
            updateById(user);

            log.info("密码重置成功，用户ID：{}，用户名：{}", userId, user.getUsername());

            // 发送密码重置成功通知邮件
            try {
                emailService.sendPasswordResetSuccessEmail(user.getEmail(), user.getUsername());
            } catch (Exception e) {
                // 邮件发送失败不影响重置操作，只记录日志
                log.warn("发送密码重置成功通知邮件失败，用户：{}，邮箱：{}", user.getUsername(), user.getEmail(), e);
            }
        } catch (Exception e) {
            log.error("重置密码失败，令牌：{}", token, e);
            throw new RuntimeException("重置密码失败：" + e.getMessage());
        }
    }
}