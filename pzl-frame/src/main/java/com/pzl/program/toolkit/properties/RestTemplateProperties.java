package com.pzl.program.toolkit.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RestTemplate 属性配置
 *
 * @author pzl
 */
@Data
@ConfigurationProperties("rest.template")
public class RestTemplateProperties {

    private Integer maxTotal;

    private Integer defaultMaxPerRoute;

    private Integer socketTimeout;

    private Integer connectTimeout;

    private Integer connectionRequestTimeout;

}
