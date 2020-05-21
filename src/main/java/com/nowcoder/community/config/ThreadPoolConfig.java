package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * @author bing  @create 2020/5/19 8:04 下午
 */
@Configuration
@EnableScheduling // 让定时任务生效的注解
@EnableAsync
public class ThreadPoolConfig {

}
