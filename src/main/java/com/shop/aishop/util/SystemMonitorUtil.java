package com.shop.aishop.util;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * 系统监控工具类，用于获取真实的CPU和内存利用率数据
 */
@Slf4j
public class SystemMonitorUtil {
    private static SystemInfo systemInfo;
    private static HardwareAbstractionLayer hal;
    private static CentralProcessor processor;
    private static GlobalMemory memory;
    private static long[] prevTicks;

    // 静态初始化块
    static {
        try {
            log.info("初始化SystemMonitorUtil...");
            systemInfo = new SystemInfo();
            log.info("SystemInfo初始化成功");
            hal = systemInfo.getHardware();
            log.info("HardwareAbstractionLayer初始化成功");
            processor = hal.getProcessor();
            log.info("CentralProcessor初始化成功");
            memory = hal.getMemory();
            log.info("GlobalMemory初始化成功");
            prevTicks = processor.getSystemCpuLoadTicks();
            log.info("prevTicks初始化成功");
            log.info("SystemMonitorUtil初始化完成");
        } catch (Exception e) {
            log.error("SystemMonitorUtil初始化失败: {}", e.getMessage());
        }
    }

    /**
     * 获取CPU使用率
     * @return CPU使用率（百分比）
     */
    public static double getCpuUsage() {
        try {
            if (processor == null) {
                log.error("processor未初始化");
                return 0;
            }
            long[] currentTicks = processor.getSystemCpuLoadTicks();
            double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100.0;
            prevTicks = currentTicks;
            // 确保返回值在合理范围内
            cpuLoad = Math.max(0, Math.min(100, cpuLoad));
            log.info("CPU使用率: {}%", cpuLoad);
            return Math.round(cpuLoad * 100) / 100.0; // 保留两位小数
        } catch (Exception e) {
            log.error("获取CPU使用率失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取内存利用率
     * @return 内存利用率（百分比）
     */
    public static double getMemoryUsage() {
        try {
            if (memory == null) {
                log.error("memory未初始化");
                return 0;
            }
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            log.info("总内存: {} bytes, 可用内存: {} bytes", totalMemory, availableMemory);
            double memoryUsage = ((totalMemory - availableMemory) / (double) totalMemory) * 100.0;
            // 确保返回值在合理范围内
            memoryUsage = Math.max(0, Math.min(100, memoryUsage));
            log.info("内存利用率: {}%", memoryUsage);
            return Math.round(memoryUsage * 100) / 100.0; // 保留两位小数
        } catch (Exception e) {
            log.error("获取内存利用率失败: {}", e.getMessage());
            return 0;
        }
    }
}