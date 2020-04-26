package com.pzl.program.juc.help;

import java.util.concurrent.CountDownLatch;

/**
 * JUC辅助类 CountDownLatch减少计数
 * JAVA并发工具类,使一个或多个线程等待其他线程完成各自的工作后再执行。
 * <p>
 * CountDownLatch主要有两个方法，当一个或多个线程调用await方法时，这些线程会阻塞。
 * 其它线程调用countDown方法会将计数器减1(调用countDown方法的线程不会阻塞)，
 * 当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行。
 *
 * @author pzl
 * @date 2020-04-04
 */
public class CountDownLatchUtil {

    /**
     * main主线程必须要等前面6个线程完成全部工作后，自己才能开干。
     */
    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        //前面六个进程全部结束后才开始执行最后的行为。
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 号离开");
                //进程数-1
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }
        //唤醒其它的进程
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t 关门");
    }

}
