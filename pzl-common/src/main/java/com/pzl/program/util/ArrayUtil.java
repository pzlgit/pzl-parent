package com.pzl.program.util;

/**
 * 数组相关的工具类
 */
public class ArrayUtil {

    /**
     * 获取一个double类型的数字的小数位有多长
     *
     * @param dd double
     * @return int
     */
    public static int doueleBitCount(double dd) {
        String temp = String.valueOf(dd);
        int i = temp.indexOf(".");
        if (i > -1) {
            return temp.length() - i - 1;
        }
        return 0;
    }

    /**
     * 获取数据内的double长度
     *
     * @param arr double数组
     * @return int[]
     */
    public static Integer[] doubleBitCount(double[] arr) {
        Integer[] len = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            len[i] = doueleBitCount(arr[i]);
        }
        return len;
    }

}
