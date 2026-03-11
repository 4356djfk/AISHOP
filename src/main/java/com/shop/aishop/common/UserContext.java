package com.shop.aishop.common;

import com.shop.aishop.entity.User;

/**
 * 用户上下文持有者（ThreadLocal 实现）
 * 用于在同一个请求线程中存储和获取用户信息
 */
public class UserContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 将用户信息存入当前线程
     */
    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 从当前线程获取用户信息
     */
    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 清理当前线程的信息（防止内存泄漏）
     */
    public static void remove() {
        USER_THREAD_LOCAL.remove();
    }

    /**
     * 判断当前用户是否为管理员
     * 逻辑示例：用户名为 admin 或者 角色字段标识为管理员
     */
    public static boolean isAdmin() {
        User user = getUser();
        return user != null && "admin".equalsIgnoreCase(user.getUsername());
    }
}
