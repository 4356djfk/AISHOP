package com.shop.aishop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.aishop.entity.User;

/**
 * <p>
 * 系统用户基础信息表 服务类
 * </p>
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息，失败则返回 null
     */
    User login(String username, String password);
}
