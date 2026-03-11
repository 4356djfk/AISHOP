package com.shop.aishop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统用户基础信息表
 * </p>
 */
@Getter
@Setter
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 账号状态: 1-正常, 0-禁用, -1-注销
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP (支持IPv6)
     */
    private String lastLoginIp;

    /**
     * 最后登录设备型号 (如: iPhone 15 Pro)
     */
    private String lastDeviceModel;

    /**
     * 最后登录操作系统 (如: iOS, Android, Windows)
     */
    private String lastOsName;

    /**
     * 最后登录浏览器 (如: Chrome, Safari)
     */
    private String lastBrowserName;

    /**
     * 累计登录次数
     */
    private Integer loginCount;

    /**
     * 累计下单次数
     */
    private Integer orderCount;

    /**
     * 累计消费金额
     */
    private BigDecimal totalSpend;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
