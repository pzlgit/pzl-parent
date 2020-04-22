package com.pzl.program.juc.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置信息
 * 使用时:直接 private  ExecutorService executorService; 注入即可
 *
 * @author pzl
 */
@Configuration
@EnableConfigurationProperties({ThreadPoolProperties.class})//使使用 @ConfigurationProperties 注解的类生效
public class ThreadPoolConfig {

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    @Bean
    public ExecutorService buildThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().build();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                threadPoolProperties.getCorePoolSize(),  //核心线程池大小
                threadPoolProperties.getMaximumPoolSize(), //最大线程数
                threadPoolProperties.getKeepAliveTime(), //空闲线程存活时间
                TimeUnit.MILLISECONDS, //时间单位
                new ArrayBlockingQueue<>(threadPoolProperties.getQueueCapacity()), //阻塞队列
                namedThreadFactory,   //线程工厂
                new ThreadPoolExecutor.AbortPolicy()); //线程池拒绝策略
        return threadPoolExecutor;
    }

}
