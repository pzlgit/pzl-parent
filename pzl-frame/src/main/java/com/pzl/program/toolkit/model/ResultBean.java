package com.pzl.program.toolkit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用的结果集封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultBean {

    private String code;

    private Object data;

    private boolean success = false;

}
