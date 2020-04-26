package com.pzl.program.toolkit.exception;

import com.pzl.program.toolkit.enums.ResultStatusCode;
import com.pzl.program.toolkit.model.ResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理Exception
 *
 * @author pzl
 * @date 2019-09-11
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle all the argument not valid exception
     *
     * @param e 异常信息
     * @return 返回错误码及消息
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResultMsg handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return new ResultMsg(ResultStatusCode.PARAMETER_ERROR.getRespCode(), "Exception:" + e.getMessage());
    }

    /**
     * Handle all the unknown exception
     *
     * @param e 异常信息
     * @return 返回错误码及消息
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultMsg internalServerError(Exception e) {
        log.error(e.getMessage(), e);
        return new ResultMsg(ResultStatusCode.Internal_Server_Error.getRespCode(), ResultStatusCode.Internal_Server_Error.getMessage());
    }

    /**
     * Handle all the user defined exception（MAException）
     *
     * @param e 异常信息
     * @return 返回错误码及消息
     */
    @ExceptionHandler(MyException.class)
    @ResponseBody
    public ResultMsg MAException(MyException e) {
        log.error(e.getMessage(), e);
        return new ResultMsg(e.getCode(), e.getMessage());
    }

}
