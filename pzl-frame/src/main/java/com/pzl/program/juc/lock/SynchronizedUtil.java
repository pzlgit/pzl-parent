package com.pzl.program.juc.lock;

/**
 * Synchronized 同步锁
 * <p>
 * 核心:
 * --1、线程操作资源类
 * --2、高内聚低耦合
 * 实现步骤:
 * --1、创建资源类
 * --2、资源类里创建同步方法、同步代码块
 *
 * @author pzl
 * @date 2020-04-04
 */
public class SynchronizedUtil {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        //创建线程,使用匿名内部类
        new Thread(() -> {
            for (int i = 0; i < 40000; i++) {
                ticket.saleMethod();
            }
        }, "thread1").start();
        //创建线程,使用匿名内部类
        new Thread(() -> {
            for (int i = 0; i < 40000; i++) {
                ticket.saleMethod();
            }
        }, "thread2").start();
    }

}

/**
 * 卖票
 */
class Ticket {

    public int num = 30;

    /**
     * 同步方法(同步锁,锁住方法中的所有东西)
     */
    public synchronized void saleMethod() {
        if (num > 0) {
            num--;
            System.out.println(Thread.currentThread().getName() + "还剩" + num + "张票");
        }
    }

    /**
     * 同步代码块(锁住代码块中的所有东西)
     */
    public void saleCode() {
        //同步代码块
        synchronized (this) {
            if (num > 0) {
                num--;
                System.out.println(Thread.currentThread().getName() + "还剩" + num + "张票");
            }
        }
    }

}
