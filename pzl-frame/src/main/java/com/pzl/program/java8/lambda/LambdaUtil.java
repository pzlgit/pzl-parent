package com.pzl.program.java8.lambda;

/**
 * lambda 表达式
 * 写法：
 * 拷贝小括号（），写死右箭头->，落地大括号{...}
 * <p>
 * lambda表达式，必须是函数式接口，必须只有一个方法，
 * 如果接口只有一个方法java默认它为函数式接口。
 * 为了正确使用Lambda表达式，需要给接口加个注解：@FunctionalInterface
 * 如有两个方法，立刻报错。
 *
 * @author pzl
 * @date 2020-04-04
 */
public class LambdaUtil {

    public static void main(String[] args) {
        LambdaUtil lambdaUtil = new LambdaUtil();
        lambdaUtil.demo();
    }

    public void demo() {
        //div 方法实现，使用lambda 表达式
        DefaultInterface defaultInterface = (x, y) -> x / y;

        System.out.println(defaultInterface.div(10, 5));
        System.out.println(defaultInterface.add(2, 3));
        System.out.println(DefaultInterface.sub(10, 5));
    }

}
