package com.shop.aishop.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Netty 业务处理器
 * 处理上线、下线、心跳、以及消息接收
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

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
            log.debug("收到客户端心跳回包");
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
