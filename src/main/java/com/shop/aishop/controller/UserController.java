package com.shop.aishop.controller;

import com.shop.aishop.entity.User;
import com.shop.aishop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 用户登录接口
     * @param loginRequest 包含 username 和 password 的 Map
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "根据用户名和密码验证用户身份，并记录登录设备信息")
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userService.login(username, password);
        
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
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
