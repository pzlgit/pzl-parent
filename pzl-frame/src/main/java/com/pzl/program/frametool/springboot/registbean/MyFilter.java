package com.pzl.program.frametool.springboot.registbean;

import javax.servlet.*;
import java.io.IOException;

/**
 * 自定义 Filter 过滤器
 *
 * @author pzl
 * @date 2020-04-16
 */
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        System.out.print("MyFilter...");
        //放行请求
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}
