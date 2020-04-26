package com.pzl.program.toolkit.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * HttpServletRequestWrapper 解决输入流只读一次丢失问题
 *
 * @author pzl
 * @date 2019-11-12
 */
@Slf4j
public class ParameterCheckServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody;
    private Charset charSet;
    private String requestBodyStr;

    public ParameterCheckServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            requestBodyStr = getRequestPostStr(request);
            if (StringUtils.isNotBlank(requestBodyStr)) {
                requestBody = requestBodyStr.getBytes(charSet);
            }
        } catch (IOException e) {
            log.error("filter param check info :{}", e);
        }
    }

    public String getRequestPostStr(HttpServletRequest request) throws IOException {
        String charSetStr = request.getCharacterEncoding();
        if (charSetStr == null) {
            charSetStr = "UTF-8";
        }
        charSet = Charset.forName(charSetStr);
        return StreamUtils.copyToString(request.getInputStream(), charSet);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (requestBody == null) {
            requestBody = new byte[0];
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

        };
    }

    /**
     * 重写 getReader()
     *
     * @throws IOException
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String getRequestBodyStr() {
        return requestBodyStr;
    }

}
