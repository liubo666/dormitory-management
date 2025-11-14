package com.dormitory.management.util;

import com.dormitory.management.context.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security工具类
 *
 * @deprecated 推荐使用 {@link UserContext} 或 {@link com.dormitory.management.service.UserContextService}
 * 这个类保留是为了向后兼容，但建议使用新的用户上下文管理方式
 *
 * 新的使用方式：
 * - 获取用户ID: {@code UserContext.getCurrentUserId()}
 * - 获取用户名: {@code UserContext.getCurrentUsername()}
 * - 获取用户信息: {@code UserContext.getCurrentUser()}
 */
@Deprecated
public final class SecurityUtil {

    private SecurityUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 获取当前认证的用户ID
     * 从Security上下文获取（作为备用方案）
     *
     * @deprecated 推荐使用 {@link UserContext#getCurrentUserId()}
     */
    @Deprecated
    public static Long getCurrentUserId() {
        // 优先从UserContext获取
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            return userId;
        }

        // UserContext中没有，从Security上下文获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 从JWT的subject中获取用户ID
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}