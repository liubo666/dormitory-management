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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private JwtUtil jwtUtil;

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
}