-- ==========================================
-- AIShop 数据库初始化脚本
-- 功能：创建用户表及相关的监控/统计字段
-- ==========================================

-- 1. 如果表已存在则删除（慎用，仅限开发初期）
DROP TABLE IF EXISTS t_user;

-- 2. 创建用户表
CREATE TABLE t_user (
                        id BIGSERIAL PRIMARY KEY,

    -- 账号核心信息
                        username VARCHAR(50) NOT NULL UNIQUE, -- 用户名（唯一）
                        password VARCHAR(255) NOT NULL,         -- 加密后的密码
                        nickname VARCHAR(50),                   -- 昵称
                        email VARCHAR(100),                     -- 邮箱
                        phone VARCHAR(20),                      -- 手机号
                        avatar_url VARCHAR(255),               -- 头像地址
                        status INT DEFAULT 1,                   -- 账号状态: 1-正常, 0-禁用, -1-注销

    -- 设备与环境监控信息
                        last_login_time TIMESTAMP,              -- 最后登录时间
                        last_login_ip VARCHAR(45),              -- 最后登录IP (支持IPv6)
                        last_device_model VARCHAR(100),         -- 最后登录设备型号 (如: iPhone 15 Pro)
                        last_os_name VARCHAR(50),               -- 最后登录操作系统 (如: iOS, Android, Windows)
                        last_browser_name VARCHAR(50),          -- 最后登录浏览器 (如: Chrome, Safari)

    -- 监控信息
                        last_heartbeat_time TIMESTAMP,          -- 最后心跳时间
                        current_cpu_usage DOUBLE PRECISION DEFAULT 0,     -- 当前CPU使用率
                        current_memory_usage DOUBLE PRECISION DEFAULT 0,  -- 当前内存利用率

    -- 统计数据
                        login_count INT DEFAULT 0,              -- 累计登录次数
                        order_count INT DEFAULT 0,              -- 累计下单次数
                        total_spend DECIMAL(12, 2) DEFAULT 0.0, -- 累计消费金额

    -- 审计信息
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP , -- 注册时间
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 最后更新时间
);

-- 3. 为表和字段添加注释（方便后续开发及协作维护）
COMMENT ON TABLE t_user IS '系统用户基础信息表';
COMMENT ON COLUMN t_user.status IS '状态: 1-启用, 0-禁用, -1-逻辑删除';
COMMENT ON COLUMN t_user.last_device_model IS '最后一次登录的硬件设备';
COMMENT ON COLUMN t_user.login_count IS '用户总登录计数';
COMMENT ON COLUMN t_user.total_spend IS '用户在平台的历史消费总额';
COMMENT ON COLUMN t_user.last_heartbeat_time IS '最后心跳时间';
COMMENT ON COLUMN t_user.current_cpu_usage IS '当前CPU使用率';
COMMENT ON COLUMN t_user.current_memory_usage IS '当前内存利用率';

-- 4. 插入示例账号 (密码建议后续在应用层加密)
INSERT INTO t_user (username, password, nickname, email, status)
VALUES ('admin', '123456', '超级管理员', 'admin@aishop.com', 1);

INSERT INTO t_user (username, password, nickname, email, status)
VALUES ('test_user', '123456', '测试号01', 'test@aishop.com', 1);