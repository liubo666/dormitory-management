package com.dormitory.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.management.dto.LoginDTO;
import com.dormitory.management.dto.LoginVO;
import com.dormitory.management.entity.UserInfo;
import com.dormitory.management.entity.SysUser;
import com.dormitory.management.vo.UserInfoVO;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息（用于ThreadLocal上下文）
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getUserInfoFromDb(Long userId);

    /**
     * 发送密码重置邮件
     * @param email 邮箱地址
     */
    void sendResetPasswordEmail(String email);

    /**
     * 重置密码
     * @param token 重置令牌
     * @param newPassword 新密码
     */
    void resetPassword(String token, String newPassword);
}