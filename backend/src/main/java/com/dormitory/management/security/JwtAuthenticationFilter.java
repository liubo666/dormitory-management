package com.dormitory.management.security;

import com.dormitory.management.util.JwtUtil;
import com.dormitory.management.service.SysUserService;
import com.dormitory.management.context.UserContext;
import com.dormitory.management.entity.UserInfo;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysUserService sysUserService;

    // 简单的用户信息缓存，避免重复查询数据库
    // Key: userId, Value: 用户信息
    private final ConcurrentHashMap<Long, UserInfo> userInfoCache = new ConcurrentHashMap<>();

    // 缓存过期时间（毫秒）- 5分钟
    private final long CACHE_EXPIRE_TIME = 5 * 60 * 1000;

    // 记录缓存时间
    private final ConcurrentHashMap<Long, Long> cacheTimestamps = new ConcurrentHashMap<>();

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 从缓存获取用户信息，如果缓存没有或过期，则从数据库查询并缓存
     * @param userId 用户ID
     * @return 用户信息
     */
    private UserInfo getUserInfoWithCache(Long userId) {
        // 检查缓存是否存在且未过期
        UserInfo cachedUserInfo = userInfoCache.get(userId);
        Long cacheTime = cacheTimestamps.get(userId);

        if (cachedUserInfo != null && cacheTime != null &&
            (System.currentTimeMillis() - cacheTime) < CACHE_EXPIRE_TIME) {
            log.debug("从缓存获取用户信息: {}", cachedUserInfo.getUsername());
            return cachedUserInfo;
        }

        // 缓存没有或过期，从数据库查询
        try {
            UserInfo userInfo = sysUserService.getUserInfoFromDb(userId);
            if (userInfo != null) {
                // 更新缓存
                userInfoCache.put(userId, userInfo);
                cacheTimestamps.put(userId, System.currentTimeMillis());
                log.debug("从数据库查询并缓存用户信息: {}", userInfo.getUsername());
            }
            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息失败，userId: {}", userId, e);
            return null;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        try {
            // 先清理上一次请求的用户上下文
            UserContext.clear();

            // 从请求头中获取Token
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                // 获取用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                if (userId != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 创建权限列表
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

                    // 创建认证对象，使用用户ID作为principal
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userId.toString(), // principal：用户ID字符串
                            null,             // credentials：通常为null
                            Collections.singletonList(authority) // authorities：用户权限
                        );

                    // 设置到Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 设置用户上下文到ThreadLocal（使用缓存）
                    try {
                        UserInfo userInfo = getUserInfoWithCache(userId);
                        if (userInfo != null) {
                            UserContext.setCurrentUser(userInfo);
                            log.debug("用户认证成功，userId: {}, username: {}, role: {}, 设置用户上下文", userId, username, role);
                        } else {
                            log.warn("无法获取用户信息，userId: {}", userId);
                        }
                    } catch (Exception e) {
                        log.error("获取用户信息失败，userId: {}", userId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT认证失败", e);
            // 清理Security上下文
            SecurityContextHolder.clearContext();
            // 清理用户上下文
            UserContext.clear();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束时清理用户上下文
            UserContext.clear();
        }
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}