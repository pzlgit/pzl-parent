package com.pzl.program.juc.help;

import java.util.concurrent.CyclicBarrier;

/**
 * JUC辅助类 CyclicBarrie循环栅栏
 * <p>
 * CyclicBarrier的字面意思是可循环（Cyclic）使用的屏障（Barrier）。它要做的事情是，
 * 让一组线程到达一个屏障（也可以叫同步点）时被阻塞，
 * 直到最后一个线程到达屏障时，屏障才会开门，所有
 * 被屏障拦截的线程才会继续干活。
 * 线程进入屏障通过CyclicBarrier的await()方法。
 *
 * @author pzl
 * @date 2020-04-04
 */
public class CyclicBarrieUtil {

    public static void main(String[] args) throws Exception {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(100, () -> {
            System.out.println("满100人订购即可开始");
        });
        for (int i = 1; i <= 100; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "\t 人");
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }

}
