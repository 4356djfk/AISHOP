package com.shop.aishop.netty;

import com.shop.aishop.util.SystemMonitorUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * Netty 业务处理器
 * 处理上线、下线、心跳、以及消息接收
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 静态初始化块，在类加载时初始化SystemMonitorUtil
    static {
        log.info("初始化SystemMonitorUtil...");
        // 调用SystemMonitorUtil的方法，触发其静态初始化
        double cpuUsage = SystemMonitorUtil.getCpuUsage();
        double memoryUsage = SystemMonitorUtil.getMemoryUsage();
        log.info("SystemMonitorUtil初始化完成，初始CPU使用率: {}%, 初始内存利用率: {}%", cpuUsage, memoryUsage);
    }

    @Autowired
    private PresenceManager presenceManager;

    /**
     * 读取客户端发送的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String text = frame.text();
        log.debug("收到 Netty 消息: {}", text);

        // 模拟登录指令示例: {"type": "LOGIN", "userId": 1}
        if (text.contains("LOGIN")) {
            // 简单解析 userId (实际建议用 JSON 库)
            try {
                String userIdStr = text.split(":")[2].replaceAll("[^0-9]", "");
                Long userId = Long.parseLong(userIdStr);
                presenceManager.userOnline(userId, ctx.channel());
            } catch (Exception e) {
                log.error("解析登录消息失败: {}", text);
            }
        } else if (text.contains("PONG")) {
            log.info("收到客户端心跳回包: {}", text);
            // 解析用户ID并更新心跳时间
            try {
                String userIdStr = text.split(":")[1].replaceAll("[^0-9]", "");
                Long userId = Long.parseLong(userIdStr);
                log.info("解析到用户ID: {}", userId);
                presenceManager.updateHeartbeat(userId);

                // 发送真实的监控数据给客户端
                log.info("开始获取系统监控数据");
                double cpuUsage = SystemMonitorUtil.getCpuUsage();
                double memoryUsage = SystemMonitorUtil.getMemoryUsage();
                log.info("获取到监控数据 - CPU: {}%, 内存: {}%", cpuUsage, memoryUsage);
                String monitorData = "MONITOR:" + userId + ":" + cpuUsage + ":" + memoryUsage;
                log.info("发送监控数据: {}", monitorData);
                ctx.writeAndFlush(new TextWebSocketFrame(monitorData));
                log.info("监控数据发送成功");
            } catch (Exception e) {
                log.error("解析心跳消息失败: {}", e.getMessage());
            }
        } else if (text.contains("MONITOR")) {
            // 处理监控信息
            try {
                String[] parts = text.split(":");
                Long userId = Long.parseLong(parts[1].replaceAll("[^0-9]", ""));
                double cpuUsage = Double.parseDouble(parts[2]);
                double memoryUsage = Double.parseDouble(parts[3]);
                presenceManager.updateMonitorInfo(userId, cpuUsage, memoryUsage);
            } catch (Exception e) {
                log.error("解析监控信息失败: {}", text);
            }
        }
    }

    /**
     * 连接建立
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("新的 WebSocket 连接已建立: {}", ctx.channel().id().asShortText());
    }

    /**
     * 连接断开
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        presenceManager.userOffline(ctx.channel());
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Netty 异常: {}", cause.getMessage());
        ctx.close();
    }

    /**
     * 心跳超时处理
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    log.warn("检测到读超时，判定用户异常离线: {}", ctx.channel().id().asShortText());
                    ctx.close(); // 触发 handlerRemoved 进行下线逻辑
                    break;
                case WRITER_IDLE:
                    // 服务端可以发送 PING 给客户端
                    ctx.writeAndFlush(new TextWebSocketFrame("PING"));
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}