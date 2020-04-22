package com.pzl.program.juc.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 线程 工具类
 * <p>
 * （1）继承thread类        （2）实现runnable接口
 * (3)  实现Callable接口   (4) Java线程池获得
 * <p>
 * 一、创建线程的方式
 * 1.1 继承 Thread : 不赞成，java是单继承，资源宝贵，要用接口方式
 * 1.2 实现runnable方法
 * class MyThread implements Runnable//新建类实现runnable接口
 * new Thread(new MyThread,...)  这种方法会新增类，有更新更好的方法
 * <p>
 * 二、创建线程
 * Thread t1 = new Thread(); 不赞成
 * new Thread(Runnable target, String name) 赞成，指定线程名
 * <p>
 * 三、匿名内部类创建线程
 * new Thread(new Runnable() {
 * public void run() {
 * }
 * }, "your thread name").start();
 * 这种方法不需要创建新的类，可以new接口
 * <p>
 * 四、lambda 表达式
 * new Thread(() -> {
 * }, "your thread name").start();
 * <p>
 * 五、最推荐创建线程方式 --使用线程池
 * 六、 面试题:callable接口与runnable接口的区别？
 * <p>
 * 答：（1）是否有返回值
 * （2）是否抛异常
 * （3）落地方法不一样，一个是run，一个是call
 * <p>
 * 七、FutureTask 未来任务
 * 未来的任务，用它就干一件事，异步调用。
 * 在主线程中需要执行比较耗时的操作时，但又不想阻塞主线程时，可以把这些作业交给Future对象在后台完成，
 * 当主线程将来需要时，就可以通过Future对象获得后台作业的计算结果或者执行状态。
 * <p>
 * 一般FutureTask多用于耗时的计算，主线程可以在完成自己的任务后，再去获取结果。
 * <p>
 * 仅在计算完成时才能检索结果；如果计算尚未完成，则阻塞 get 方法。一旦计算完成，
 * 就不能再重新开始或取消计算。get方法而获取结果只有在计算完成时获取，否则会一直阻塞直到任务转入完成状态，
 * 然后会返回结果或者抛出异常。
 * <p>
 * 只计算一次，get方法放到最后
 *
 * @author pzl
 * @date 2020-04-04
 */
public class ThreadUtil {

    public static void main(String[] args) throws Exception {
        //创建线程,使用匿名内部类
        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                System.out.println(i);
            }
        }, "thread1").start();
        //创建线程,使用匿名内部类
        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                System.out.println(i);
            }
        }, "thread2").start();
        //注：阿里巴巴插件以及有些插件已经不推荐使用这种方式创建线程，推荐使用线程池。

        ThreadUtil threadUtil = new ThreadUtil();
        threadUtil.callableCompareRunable();
    }

    /**
     * callable接口与runnable接口的区别
     */
    public void callableCompareRunable() throws Exception {
        FutureTask<Object> ft = new FutureTask<Object>(new callable());
        new Thread(ft, "AA").start();
        //判断未来任务是否完成,如果没完成，则一直等待，直到完成获取get中的值。
        while (!ft.isDone()) {
            System.out.println("wait ...");
        }
        //最后再来获取值，只能获取一次。可用于调用多个方法然后最后统一做运算。
        Object o = ft.get();
        System.out.println(o);

        new Thread(new runable(), "BB").start();
    }

}

class runable implements Runnable {

    @Override
    public void run() {
        System.out.println("runable");
    }

}

class callable implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        System.out.println("callable");
        return "success";
    }

}