package com.pzl.program.util;

/**
 * 数据判断工具类
 */
public class AssertUtil {

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param message 信息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
