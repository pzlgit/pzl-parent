package com.pzl.program.toolkit.exception;

/**
 * 自定义异常Exception
 *
 * @author pzl
 * @date 2019-09-11
 */
public class MyException extends Exception {

    public MyException() {
        super();
    }

    /**
     * 实例化构造
     *
     * @param code    状态码
     * @param message 消息
     */
    public MyException(int code, String message) {
        super(message);
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
