package com.dormitory.management.context;

import com.dormitory.management.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户上下文管理器
 * 使用ThreadLocal存储当前请求的用户信息，避免每次都查询数据库
 */
@Slf4j
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     * @param userInfo 用户信息
     */
    public static void setCurrentUser(UserInfo userInfo) {
        USER_CONTEXT.set(userInfo);
        log.debug("设置用户上下文: {}", userInfo != null ? userInfo.getUsername() : "null");
    }

    /**
     * 获取当前用户信息
     * @return 用户信息，可能为null
     */
    public static UserInfo getCurrentUser() {
        UserInfo userInfo = USER_CONTEXT.get();
        log.debug("获取用户上下文: {}", userInfo != null ? userInfo.getUsername() : "null");
        return userInfo;
    }

    /**
     * 获取当前用户ID
     * @return 用户ID，可能为null
     */
    public static Long getCurrentUserId() {
        UserInfo userInfo = getCurrentUser();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 获取当前用户名
     * @return 用户名，可能为null
     */
    public static String getCurrentUsername() {
        UserInfo userInfo = getCurrentUser();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    /**
     * 获取当前用户真实姓名
     * @return 真实姓名，可能为null
     */
    public static String getCurrentRealName() {
        UserInfo userInfo = getCurrentUser();
        return userInfo != null ? userInfo.getRealName() : null;
    }

    /**
     * 清除当前用户上下文
     * 通常在请求结束时调用
     */
    public static void clear() {
        USER_CONTEXT.remove();
        log.debug("清除用户上下文");
    }

    /**
     * 检查是否有用户登录
     * @return true如果有用户登录，false否则
     */
    public static boolean hasUser() {
        return getCurrentUser() != null;
    }
}