package com.shop.aishop.netty;

import com.shop.aishop.entity.User;
import com.shop.aishop.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    // 用户 ID -> 监控信息
    private static final Map<Long, UserMonitorInfo> USER_MONITOR_INFO = new ConcurrentHashMap<>();

    /**
     * 用户监控信息
     */
    private static class UserMonitorInfo {
        private LocalDateTime lastHeartbeatTime;
        private double cpuUsage;
        private double memoryUsage;
        private String ip;

        // 构造方法
        public UserMonitorInfo(LocalDateTime lastHeartbeatTime, double cpuUsage, double memoryUsage, String ip) {
            this.lastHeartbeatTime = lastHeartbeatTime;
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.ip = ip;
        }

        // getter and setter
        public LocalDateTime getLastHeartbeatTime() { return lastHeartbeatTime; }
        public void setLastHeartbeatTime(LocalDateTime lastHeartbeatTime) { this.lastHeartbeatTime = lastHeartbeatTime; }
        public double getCpuUsage() { return cpuUsage; }
        public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
        public double getMemoryUsage() { return memoryUsage; }
        public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
    }

    /**
     * 用户上线
     */
    public void userOnline(Long userId, Channel channel) {
        USER_CHANNELS.put(userId, channel);
        CHANNEL_TO_USER.put(channel.id().asLongText(), userId);

        // 初始化用户监控信息
        String ip = channel.remoteAddress() != null ? channel.remoteAddress().toString() : "未知";
        USER_MONITOR_INFO.put(userId, new UserMonitorInfo(LocalDateTime.now(), 0, 0, ip));

        // 更新数据库状态为在线 (1) 和最后心跳时间
        User user = new User();
        user.setId(userId);
        user.setStatus(1);
        user.setLastHeartbeatTime(LocalDateTime.now());
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

            // 移除监控信息
            USER_MONITOR_INFO.remove(userId);

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

    /**
     * 更新用户心跳时间
     */
    public void updateHeartbeat(Long userId) {
        UserMonitorInfo info = USER_MONITOR_INFO.get(userId);
        if (info != null) {
            info.setLastHeartbeatTime(LocalDateTime.now());

            // 更新数据库中的心跳时间
            User user = new User();
            user.setId(userId);
            user.setLastHeartbeatTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    /**
     * 更新用户监控信息
     */
    public void updateMonitorInfo(Long userId, double cpuUsage, double memoryUsage) {
        UserMonitorInfo info = USER_MONITOR_INFO.get(userId);
        if (info != null) {
            info.setCpuUsage(cpuUsage);
            info.setMemoryUsage(memoryUsage);

            // 更新数据库中的监控信息
            User user = new User();
            user.setId(userId);
            user.setCurrentCpuUsage(cpuUsage);
            user.setCurrentMemoryUsage(memoryUsage);
            userMapper.updateById(user);
        }
    }

    /**
     * 获取用户监控信息
     */
    public Map<String, Object> getUserMonitorInfo(Long userId) {
        UserMonitorInfo info = USER_MONITOR_INFO.get(userId);
        if (info != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("lastHeartbeatTime", info.getLastHeartbeatTime());
            result.put("cpuUsage", info.getCpuUsage());
            result.put("memoryUsage", info.getMemoryUsage());
            result.put("ip", info.getIp());
            return result;
        }
        return null;
    }

    /**
     * 获取所有在线用户的监控信息
     */
    public Map<Long, Map<String, Object>> getAllUserMonitorInfo() {
        Map<Long, Map<String, Object>> result = new HashMap<>();
        for (Long userId : USER_CHANNELS.keySet()) {
            Map<String, Object> info = getUserMonitorInfo(userId);
            if (info != null) {
                result.put(userId, info);
            }
        }
        return result;
    }
}
