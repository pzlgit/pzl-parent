package com.pzl.program.toolkit.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author pzl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ResultData", description = "结果集合")
public class ResultData<T> implements Serializable {

    private static final long serialVersionUID = -4640326508424681109L;

    /**
     * 查询数据总条数
     */
    private Integer total;

    /**
     * 返回数据集合
     */
    private List<T> data;

    public static <T> ResultData<T> ok(List<T> data, Integer total) {
        return new ResultData<>(total, data);
    }

}
