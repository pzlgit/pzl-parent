package com.pzl.program.frametool.springboot.registbean;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 组件注册
 * <p>
 * 之前我们是在web.xml中注册这三大组件，
 * 由于SpringBoot默认是以jar包的方式启动嵌入式的Servlet容器，
 * 以此来启动SpringBoot的web应用，并没有web.xml文件，
 * 所以在SpringBoot中我们需要使用
 * ServletRegistrationBean 、FilterRegistrationBean和ServletListenerRegistrationBean
 * 来分别注册Servlet、Filter和Listener
 *
 * @author pzl
 * @date 2020-04-16
 */
@Configuration
public class MyWebConfig {

    /**
     * servlet容器注册
     * 访问：localhost:8080/myServlet  会返回 MyServlet 信息
     * 默认拦截： /  所有请求；包含静态资源，但是不拦截jsp请求；
     * /* 则会拦截jsp
     * 可以通过server.servletPath来修改SpringMVC前端控制器默认拦截的请求路径
     *
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean myServlet() {
        //MyServlet 自定义的servlet ， /myServlet 映射地址
        ServletRegistrationBean servletRegistrationBean =
                new ServletRegistrationBean(new MyServlet(), "/myServlet");
        return servletRegistrationBean;
    }

    /**
     * Filter 过滤器注册
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean myFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new MyFilter());
        //注册Filter时可以指定拦截哪些请求，也可以指定拦截哪些Servlet，
        // 可以通过构造器直接设置，也可以跟案例一样分步设置。
        registrationBean.setUrlPatterns(Arrays.asList("/hello", "/myServlet"));
        return registrationBean;
    }

    /**
     * 注册监听器
     *
     * @return ServletListenerRegistrationBean
     */
    @Bean
    public ServletListenerRegistrationBean myListener() {
        ServletListenerRegistrationBean servletListenerRegistrationBean =
                new ServletListenerRegistrationBean<>(new MyListener());
        return servletListenerRegistrationBean;
    }

}