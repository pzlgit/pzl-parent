package com.pzl.program.toolkit.model;

import com.pzl.program.toolkit.enums.ResultStatusCode;
import lombok.Data;

/**
 * 返回数据结构 ResultMsg
 *
 * @param <T> 参数类型
 * @author pzl
 */
@Data
public class ResultMsg<T> {

    /**
     * 响应码
     */
    private Integer resCode;

    /**
     * 响应消息
     */
    private String resMsg;

    /**
     * 响应Data
     */
    private T resData;

    /**
     * Ok result msg.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the result msg
     */
    public static <T> ResultMsg ok(T data) {
        return new ResultMsg<>(data);
    }

    /**
     * Error result msg.
     *
     * @param <T>       the type parameter
     * @param errorCode the error code
     * @param respMsg   the resp msg
     * @return the result msg
     */
    public static <T> ResultMsg error(Integer errorCode, String respMsg) {
        return new ResultMsg<>(errorCode, respMsg, null);
    }

    /**
     * paramError result msg.
     *
     * @param respMsg the resp msg
     * @param <T>     the type parameter
     * @return the result msg
     */
    public static <T> ResultMsg paramError(String respMsg) {
        return new ResultMsg<>(ResultStatusCode.PARAMETER_ERROR.getRespCode(),
                respMsg, null);
    }

    /**
     * Result result msg.
     *
     * @param <T>       the type parameter
     * @param errorCode the error code
     * @param respMsg   the resp msg
     * @param data      the data
     * @param total     the total
     * @return the result msg
     */
    public static <T> ResultMsg result(Integer errorCode, String respMsg, T data, int total) {
        return new ResultMsg<>(errorCode, respMsg, data);
    }

    /**
     * Instantiates a new private Result msg.
     *
     * @param data the data
     */
    private ResultMsg(T data) {
        this.resCode = ResultStatusCode.SUCCESS.getRespCode();
        this.resMsg = ResultStatusCode.SUCCESS.getMessage();
        this.resData = data;
    }

    /**
     * Instantiates a new Result msg.
     *
     * @param errorCode the error code
     * @param respMsg   the resp msg
     * @param data      the data
     */
    public ResultMsg(Integer errorCode, String respMsg, T data) {
        this.resCode = errorCode;
        this.resMsg = respMsg;
        this.resData = data;
    }

    /**
     * Instantiates a new Result msg.
     *
     * @param resCode the resp code
     * @param resMsg  the resp msg
     */
    public ResultMsg(Integer resCode, String resMsg) {
        this.resCode = resCode;
        this.resMsg = resMsg;
    }

}
