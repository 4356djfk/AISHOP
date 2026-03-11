package com.shop.aishop.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shop.aishop.entity.User;
import com.shop.aishop.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户拦截器
 * 用于在请求开始前将用户信息存入 UserContext，在请求结束后清理
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 优先从 Header 中获取用户 ID (模拟登录态/调试用)
        String userIdStr = request.getHeader("X-User-Id");
        
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdStr);
                User user = userMapper.selectById(userId);
                if (user != null) {
                    UserContext.setUser(user);
                    return true;
                }
            } catch (NumberFormatException e) {
                // ID 格式错误，继续尝试 Session
            }
        }

        // 2. 如果 Header 没有，则从 Session 中获取 (正式登录流程)
        User sessionUser = (User) request.getSession().getAttribute("LOGIN_USER");
        if (sessionUser != null) {
            UserContext.setUser(sessionUser);
        }
        
        return true; 
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后务必从 ThreadLocal 中删除，防止内存泄漏
        UserContext.remove();
    }
}
