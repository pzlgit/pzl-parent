package com.pzl.program.toolkit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 状态码定义
 *
 * @author pzl
 * @date 2019-9-7
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public enum ResultStatusCode {

    /**
     * SUCCESS
     */
    SUCCESS(0, "成功"),
    /**
     * FAILURE
     */
    FAILURE(1, "失败"),
    /**
     * PARAMETER_ERROR
     */
    PARAMETER_ERROR(2, "参数错误"),
    /**
     * Internal_Server_Error
     */
    Internal_Server_Error(3, "内部调用异常"),
    /**
     * Server_Error
     */
    Server_Error(4, "服务器异常");

    /**
     * 服务响应状态码
     */
    private Integer respCode;
    /**
     * 服务响应状态信息
     */
    private String message;

}
