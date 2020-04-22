package com.pzl.program.toolkit.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author pzl
 * @date 2019-10-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ResultPage", description = "结果集合")
public class ResultPage<T> implements Serializable {

    private static final long serialVersionUID = -4640326508424681109L;

    /**
     * 查询数据总条数
     */
    private Integer total;

    /**
     * 当前页号码
     */
    private Integer pageNo;

    /**
     * 每页显示的数据条数
     */
    private Integer pageSize;

    /**
     * 返回数据集合
     */
    private T data;

}
