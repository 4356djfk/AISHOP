package com.shop.aishop.netty;

import com.shop.aishop.entity.User;
import com.shop.aishop.mapper.UserMapper;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线状态管理器
 * 用于记录当前在线的用户及其对应的 Netty 通道
 */
@Slf4j
@Component
public class PresenceManager {

    @Autowired
    private UserMapper userMapper;

    // 用户 ID -> Netty 通道
    private static final Map<Long, Channel> USER_CHANNELS = new ConcurrentHashMap<>();
    
    // 通道 ID -> 用户 ID (用于下线时快速查找)
    private static final Map<String, Long> CHANNEL_TO_USER = new ConcurrentHashMap<>();

    /**
     * 用户上线
     */
    public void userOnline(Long userId, Channel channel) {
        USER_CHANNELS.put(userId, channel);
        CHANNEL_TO_USER.put(channel.id().asLongText(), userId);
        
        // 更新数据库状态为在线 (1)
        User user = new User();
        user.setId(userId);
        user.setStatus(1);
        userMapper.updateById(user);
        
        log.info("用户 [{}] 建立 Netty 连接，同步数据库上线状态，当前内存在线人数: {}", userId, USER_CHANNELS.size());
    }

    /**
     * 用户下线
     */
    public void userOffline(Channel channel) {
        String channelId = channel.id().asLongText();
        Long userId = CHANNEL_TO_USER.remove(channelId);
        if (userId != null) {
            USER_CHANNELS.remove(userId);
            
            // 更新数据库状态为离线 (0)
            User user = new User();
            user.setId(userId);
            user.setStatus(0);
            userMapper.updateById(user);
            
            log.info("用户 [{}] Netty 断开，同步数据库下线状态，当前内存在线人数: {}", userId, USER_CHANNELS.size());
        }
    }

    /**
     * 获取在线人数
     */
    public int getOnlineCount() {
        return USER_CHANNELS.size();
    }

    /**
     * 判断用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        return USER_CHANNELS.containsKey(userId);
    }
}
