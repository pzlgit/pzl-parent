package com.pzl.program.toolkit.interceptor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 过滤器
 *
 * @author pzl
 * @date 2019-11-12
 */
public class ParamsReqeustBodyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new ParameterCheckServletRequestWrapper((HttpServletRequest) request);
        }
        if (requestWrapper == null) {
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {
        // Do nothing
    }

}
