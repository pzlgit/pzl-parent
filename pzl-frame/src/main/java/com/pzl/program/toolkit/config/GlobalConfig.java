package com.pzl.program.toolkit.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 全局配置信息
 *
 * @author pzl
 * @date 2020-02-20
 */
@Data
@Component
@ConfigurationProperties(prefix = "Global.config")
public class GlobalConfig implements InitializingBean {

    /**
     * 机器ip
     */
    private String ip;
    public static String IP = "www.pzl.com";

    /**
     * http ip
     */
    private String httpIp;
    public static String HTTP_IP = "http://www.pzl.com";

    @Override
    public void afterPropertiesSet() throws Exception {
        IP = ip;
        HTTP_IP = httpIp;
    }

}
