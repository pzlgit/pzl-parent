package com.pzl.program.toolkit.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化数据为map
 *
 * @author pzl
 * @date 2020-03-25
 */
@Slf4j
@Configuration
public class InitMap implements InitializingBean {

    @Autowired
    private RestTemplate restTemplate;

    public static final Map<String, Object> GLOBAl_MAP = new HashMap<>(16);

    @Override
    public void afterPropertiesSet() throws Exception {
        GLOBAl_MAP.put("key", "value");
    }

}
