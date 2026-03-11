package com.shop.aishop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.aishop.entity.User;
import com.shop.aishop.mapper.UserMapper;
import com.shop.aishop.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 系统用户基础信息表 服务实现类
 * </p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        // 1. 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);

        // 2. 校验密码 (目前为明文比对，建议生产环境使用 BCrypt)
        if (user != null && user.getPassword().equals(password)) {
            // 3. 更新统计信息与监控数据 (模拟)
            user.setLoginCount((user.getLoginCount() == null ? 0 : user.getLoginCount()) + 1);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp("127.0.0.1"); // 此处在实际项目中可从 Request 获取
            user.setLastOsName("Windows 10");
            user.setLastBrowserName("Chrome");
            
            this.updateById(user);
            return user;
        }

        return null;
    }
}
