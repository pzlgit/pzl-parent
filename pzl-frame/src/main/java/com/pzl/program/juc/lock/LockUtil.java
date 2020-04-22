package com.pzl.program.juc.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock 锁工具类
 * <p>
 * java.util.concurrent.locks 锁实现提供了比使用同步方法和语句可以获得的更广泛的锁操作。
 * 它们允许更灵活的结构，可能具有非常不同的属性，并且可能支持多个关联的条件对象。
 * <p>
 * 一、线程间通信
 * 1、生产者+消费者   通知等待唤醒机制
 * 2、判断    while   cd1.await(); 2、干活   3、通知 cd2.signal();
 * 3、防止虚假唤醒用while
 * <p>
 * 二、多线程定制化调用通信
 * <p>
 * 三、锁的问题分析
 * A 、一个对象里面如果有多个synchronized方法，某一个时刻内，只要一个线程去调用其中的一个synchronized方法了，
 * 其它的线程都只能等待，换句话说，某一个时刻内，只能有唯一一个线程去访问这些synchronized方法。
 * 锁的是当前对象this，被锁定后，其它的线程都不能进入到当前对象的其它的synchronized方法。
 * B、加个普通方法后发现和同步锁无关
 * C、成两个对象后，不是同一把锁了，情况立刻变化。
 * D、synchronized实现同步的基础：Java中的每一个对象都可以作为锁。
 * 具体表现为以下3种形式。
 * --对于普通同步方法，锁是当前实例对象。
 * --对于静态同步方法，锁是当前类的Class对象。
 * --对于同步方法块，锁是Synchonized括号里配置的对象。
 * <p>
 * 当一个线程试图访问同步代码块时，它首先必须得到锁，退出或抛出异常时必须释放锁。
 * 也就是说如果一个实例对象的非静态同步方法获取锁后，该实例对象的其他非静态同步方法必须等待获取锁的方法释放锁后才能获取锁，
 * 可是别的实例对象的非静态同步方法因为跟该实例对象的非静态同步方法用的是不同的锁，
 * 所以毋须等待该实例对象已获取锁的非静态同步方法释放锁就可以获取他们自己的锁。
 * <p>
 * 所有的静态同步方法用的也是同一把锁——类对象本身，这两把锁是两个不同的对象，所以静态同步方法与非静态同步方法之间是不会有竞态条件的。
 * 但是一旦一个静态同步方法获取锁后，其他的静态同步方法都必须等待该方法释放锁后才能获取锁，而不管是同一个实例对象的静态同步方法之间，
 * 还是不同的实例对象的静态同步方法之间，只要它们同一个类的实例对象！
 *
 * @author pzl
 * @date 2020-04-04
 */
public class LockUtil {

    /**
     * 可重复锁 ReentrantLock
     */
    private final ReentrantLock lock = new ReentrantLock();

    public void demo() {
        // block until condition holds 上锁
        lock.lock();
        try {
            // method body
        } finally {
            lock.unlock(); //解锁
        }
    }

    public static void main(String[] args) {
        LockUtil lockUtil = new LockUtil();
        lockUtil.testShareResource();
    }

    /**
     * 多线程之间按顺序调用，实现A->B->C
     * 三个线程启动，要求如下：
     * <p>
     * AA打印5次，BB打印10次，CC打印15次
     * 接着
     * AA打印5次，BB打印10次，CC打印15次
     * ......来10轮
     */
    public void testShareResource() {
        ShareResource sr = new ShareResource();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                sr.print5(i);
            }
        }, "AA").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                sr.print10(i);
            }
        }, "BB").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                sr.print15(i);
            }
        }, "CC").start();
    }


    /**
     * 线程间通信：cd.await(); cd.signalAll();
     * 现在两个线程
     * 操作一个初始值为0的变量
     * 实现一个线程对变量增加1，一个线程对变量减少1
     * 交替，来10轮
     * <p>
     * 模板上：线程 操作 资源类
     * 模板中：判断 干活 通知
     * 模板下：注意多线程之间的虚假唤醒
     */
    public void testShareDataOne() {
        ShareDataOne shareDataOne = new ShareDataOne();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareDataOne.incr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "AA").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareDataOne.decr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "BB").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareDataOne.incr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "CC").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    shareDataOne.decr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "DD").start();
    }

}


/**
 * 两个线程打印
 */
class ShareDataOne {

    //初始值为零的变量
    private int number = 0;
    Lock lock = new ReentrantLock();
    Condition cd = lock.newCondition();

    public void incr() throws InterruptedException {
        lock.lock();
        try {
            //判断，使用while，防止虚假唤醒
            while (number != 0) {
                cd.await();
            }
            //干活
            number++;
            System.out.println(Thread.currentThread().getName() + "\t" + number);
            //通知
            cd.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public synchronized void decr() throws InterruptedException {
        lock.lock();
        try {
            //判断，使用while，防止虚假唤醒
            while (number != 1) {
                cd.await();
            }
            //干活
            number--;
            System.out.println(Thread.currentThread().getName() + "\t" + number);
            //通知
            cd.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}


/**
 * 多线程定制化通信
 */
class ShareResource {

    private int number = 1;    //1:A 2:B 3:C

    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void print5(int totalLoopNumber) {
        lock.lock();
        try {
            //1 判断
            while (number != 1) {
                //A 就要停止
                c1.await();
            }
            //2 干活
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "\t" + i + "\t totalLoopNumber: " + totalLoopNumber);
            }
            //3 通知
            number = 2;
            c2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print10(int totalLoopNumber) {
        lock.lock();
        try {
            //1 判断
            while (number != 2) {
                //A 就要停止
                c2.await();
            }
            //2 干活
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "\t" + i + "\t totalLoopNumber: " + totalLoopNumber);
            }
            //3 通知
            number = 3;
            c3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print15(int totalLoopNumber) {
        lock.lock();
        try {
            //1 判断
            while (number != 3) {
                //A 就要停止
                c3.await();
            }
            //2 干活
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "\t" + i + "\t totalLoopNumber: " + totalLoopNumber);
            }
            //3 通知
            number = 1;
            c1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}