package com.shop.aishop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shop.aishop.common.UserContext;
import com.shop.aishop.dto.LoginRequest;
import com.shop.aishop.entity.User;
import com.shop.aishop.mapper.UserMapper;
import com.shop.aishop.netty.PresenceManager;
import com.shop.aishop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户控制层
 * </p>
 */
@Tag(name = "用户管理", description = "包含用户登录、注册等接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PresenceManager presenceManager;

    /**
     * 获取当前在线用户列表 (数据库持久化统计)
     */
    @Operation(summary = "获取在线用户列表", description = "从数据库查询所有状态为 1 (在线) 的用户信息")
    @GetMapping("/online-list")
    public Map<String, Object> getOnlineList() {
        // 查询所有状态为 1 的用户
        List<User> list = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
        
        // 简化返回信息，只返回 ID 和用户名
        List<Map<String, Object>> userList = list.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "获取成功");
        result.put("data", userList);
        return result;
    }

    /**
     * 获取当前在线总人数 (数据库持久化统计)
     */
    @Operation(summary = "获取在线人数", description = "从数据库查询当前状态为 1 (在线) 的总人数")
    @GetMapping("/online-count")
    public Map<String, Object> getOnlineCount() {
        // 从数据库实时查询在线状态
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "获取成功");
        result.put("data", count);
        return result;
    }

    /**
     * 用户退出登录接口
     */
    @Operation(summary = "用户退出", description = "清理 Session 并同步数据库状态为离线")
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user != null) {
            // 1. 同步数据库状态
            User updateUserInfo = new User();
            updateUserInfo.setId(user.getId());
            updateUserInfo.setStatus(0);
            userMapper.updateById(updateUserInfo);
            
            // 2. 清理 Session
            session.removeAttribute("LOGIN_USER");
            session.invalidate();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "退出成功");
        return result;
    }

    /**
     * 获取当前登录用户信息 (ThreadLocal 隔离测试)
     */
    @Operation(summary = "获取当前用户信息", description = "通过 ThreadLocal 获取拦截器注入的当前用户信息，测试隔离性")
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        User user = UserContext.getUser();
        boolean isAdmin = UserContext.isAdmin();
        
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("code", 200);
            result.put("msg", isAdmin ? "当前是管理员" : "当前是普通用户");
            result.put("data", user);
        } else {
            result.put("code", 401);
            result.put("msg", "未检测到登录用户，请先登录或在 Header 中添加 X-User-Id");
        }
        return result;
    }

    /**
     * 用户登录接口
     * @param loginRequest 登录请求对象
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "根据用户名和密码验证用户身份，并记录登录设备信息")
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.login(username, password);
        
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            // 登录成功，存入 Session
            session.setAttribute("LOGIN_USER", user);
            
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", user);
        } else {
            result.put("code", 400);
            result.put("msg", "用户名或密码错误");
        }
        
        return result;
    }
}
