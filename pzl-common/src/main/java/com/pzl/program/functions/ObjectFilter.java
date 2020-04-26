package com.pzl.program.functions;

/**
 * 对象过滤接口
 *
 * @param <T> 任意对象
 */
public interface ObjectFilter<T> {

    boolean filter(T t);

}
