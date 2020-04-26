package com.pzl.program;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import io.prometheus.client.spring.web.EnablePrometheusTiming;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 场景启动器类
 *
 * @author pzl
 */
@EnableSwagger2 //开启swagger2自动生成api文档的功能
@EnableScheduling //定时任务注解
@SpringBootApplication //SpringBoot启动注解
@EnablePrometheusTiming
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
@MapperScan(value = "com.pzl.program.frametool.mysql.mybatis")
public class PzlFrameApplication {

    public static void main(String[] args) {
        SpringApplication.run(PzlFrameApplication.class, args);
    }

}
