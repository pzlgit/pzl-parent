package com.pzl.program.constant;

/**
 * 全局常量类
 *
 * @author pzl
 * @date 2019-11-24
 */
public class GlobalConstant {

    /**
     * 省份正则表达式规则
     */
    private static final String PROVINCE_REGEX = "\\d{2}0000";

    /**
     * 城市正则表达式规则
     */
    private static final String CITY_REGEX = "\\d{4}00";

    /**
     * 城市编码六位数字验证正则表达式规则
     */
    private static final String CITY_CODE_REGEX = "\\d{6}";

    /**
     * 数值非负数验证规则(正整数和0可通过验证)
     */
    private static final String NUMBER_INT_REGEX = "^[1-9]\\d*|0$";

    /**
     * 判断是否为数字
     */
    private static final String NUMBER_REGEX = "[0-9]*";

    public static String getNumberRegex() {
        return NUMBER_REGEX;
    }

    public static String getProvinceRegex() {
        return PROVINCE_REGEX;
    }

    public static String getCityRegex() {
        return CITY_REGEX;
    }

    public static String getCityCodeRegex() {
        return CITY_CODE_REGEX;
    }

    public static String getNumberIntRegex() {
        return NUMBER_INT_REGEX;
    }

}
