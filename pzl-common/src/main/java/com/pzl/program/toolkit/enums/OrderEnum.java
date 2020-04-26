package com.pzl.program.toolkit.enums;

/**
 * 排序类型
 *
 * @author pzl
 */
public enum OrderEnum {

    /**
     * 升序
     */
    ASC("ASC"),

    /**
     * 降序
     */
    DESC("DESC");

    private String text;

    OrderEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * 判断数据是否在枚举中
     *
     * @param text 数据
     * @return boolean
     */
    public static boolean isInEnum(String text) {
        for (OrderEnum order : OrderEnum.values()) {
            if (order.getText().equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

}
