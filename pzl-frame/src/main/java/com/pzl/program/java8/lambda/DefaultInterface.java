package com.pzl.program.java8.lambda;

/**
 * 接口里在java8后容许有接口的实现，default方法默认实现
 * <p>
 * default int div(int x,int y) {
 * return x/y;
 * }
 * <p>
 * 允许使用静态方法，可直接用类名.方法名进行调用
 *
 * @author pzl
 * @date 2020-04-04
 */
@FunctionalInterface
interface DefaultInterface {

    int div(int x, int y);

    /**
     * java8新特性，default方法默认实现,可以写多个
     */
    default int add(int x, int y) {
        return x + y;
    }

    /**
     * java8新特性，静态方法实现,可以写多个
     */
    static int sub(int x, int y) {
        return x - y;
    }

}
