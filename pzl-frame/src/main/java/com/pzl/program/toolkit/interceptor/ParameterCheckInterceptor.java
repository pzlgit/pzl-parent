package com.pzl.program.toolkit.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.pzl.program.toolkit.enums.ResultStatusCode;
import com.pzl.program.toolkit.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 参数校验拦截器
 *
 * @author pzl
 * @date 2019-11-12
 */
@Slf4j
public class ParameterCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object o) throws Exception {
        String path = request.getRequestURI();
        log.info("请求路径-path={}", path);
        if (request instanceof ParameterCheckServletRequestWrapper) {
            String requestBodyStr = ((ParameterCheckServletRequestWrapper) request).getRequestBodyStr();
            if (StringUtils.isNotBlank(requestBodyStr)) {
                try {
                    if (JSON.isValidObject(requestBodyStr)) {
                        JSONObject obj = JSONObject.parseObject(requestBodyStr);
                        log.info("拦截参数={}", JSONObject.toJSONString(obj));
                    }
                } catch (JSONException e) {
                    log.error("convert json fail {},str={}", e, requestBodyStr);
                    throw new MyException(ResultStatusCode.PARAMETER_ERROR.getRespCode(), e.getMessage());
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {
        // Do nothing
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
        // Do nothing
    }

}
