package com.pzl.program.toolkit.interceptor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置信息
 *
 * @author pzl
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * 设置可访问静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 拦截器注册
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getParamCheckInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public ParameterCheckInterceptor getParamCheckInterceptor() {
        return new ParameterCheckInterceptor();
    }

    @Bean
    public FilterRegistrationBean setFilter() {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(new ParamsReqeustBodyFilter());
        filterBean.setName("myFilter");
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

}
