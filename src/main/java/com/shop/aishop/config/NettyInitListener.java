package com.shop.aishop.config;

import com.shop.aishop.netty.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

/**
 * Netty 初始化监听器
 * 在 Spring Boot 启动完成后自动启动 Netty 服务
 */
@Component
public class NettyInitListener implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(String... args) {
        // 在 8888 端口启动 Netty WebSocket 服务
        nettyServer.start(8888);
    }

    @PreDestroy
    public void destroy() {
        nettyServer.stop();
    }
}
