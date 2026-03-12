package com.shop.aishop.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Netty WebSocket 服务器
 */
@Slf4j
@Component
public class NettyServer {

    @Autowired
    private NettyServerHandler nettyServerHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * 启动 Netty 服务器
     */
    public void start(int port) {
        new Thread(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                // 添加空闲状态处理器，设置读空闲和写空闲时间
                                ch.pipeline().addLast(new IdleStateHandler(30, 10, 0, TimeUnit.SECONDS));
                                // HTTP 编解码器
                                ch.pipeline().addLast(new HttpServerCodec());
                                // 支持大文件传输
                                ch.pipeline().addLast(new ChunkedWriteHandler());
                                // 聚合 HTTP 请求
                                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                                // WebSocket 协议处理器
                                ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                                // 自定义业务处理器
                                ch.pipeline().addLast(nettyServerHandler);
                            }
                        });

                log.info("Netty WebSocket 服务器正在启动，端口: {}", port);
                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("Netty 服务器启动异常", e);
                Thread.currentThread().interrupt();
            } finally {
                stop();
            }
        }).start();
    }

    /**
     * 关闭 Netty 服务器
     */
    public void stop() {
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        log.info("Netty WebSocket 服务器已关闭");
    }
}