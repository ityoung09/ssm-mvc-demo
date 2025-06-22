package com.kedaya.task;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.task
 * @Project：ssm-mvc-demo
 * @name：UserSyncTask
 * @Date：2025-06-22 00:26
 * @Filename：UserSyncTask
 */
@Component
@Slf4j
public class UserSyncTask {

    @PostConstruct
    public void init() {
        System.out.println("✅ 定时任务组件已加载");
        // 输出当前环境
        System.out.println("当前环境：" + System.getProperty("spring.profiles.active"));
    }

    // 每隔10秒执行一次
    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncUsers() {
        log.debug("定时任务开始执行：{}", LocalDateTime.now());
        log.info("定时任务开始执行：{}", LocalDateTime.now());
        log.warn("定时任务开始执行：{}", LocalDateTime.now());
        log.error("定时任务开始执行：{}", LocalDateTime.now());
    }
}
