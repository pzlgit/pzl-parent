package com.pzl.program.juc.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池配置属性类
 *
 * @author pzl
 */
@Data
@ConfigurationProperties("thread.pool")
public class ThreadPoolProperties {

    //核心线程数
    private int corePoolSize;

    //最大线程数
    private int maximumPoolSize;

    //空闲线程存活时间
    private long keepAliveTime;

    //阻塞队列容量
    private int queueCapacity;

}
