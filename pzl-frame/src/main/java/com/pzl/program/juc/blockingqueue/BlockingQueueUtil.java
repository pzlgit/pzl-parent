package com.pzl.program.juc.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * BlockingQueue 阻塞队列 (使用在线程池中的技术)
 * <p>
 * 栈：先进后出，后进先出
 * 队列：先进先出
 * <p>
 * 当队列是空的，从队列中获取元素的操作将会被阻塞。
 * 当队列是满的，从队列中添加元素的操作将会被阻塞。
 * <p>
 * 试图从空的队列中获取元素的线程将会被阻塞，直到其他线程往空的队列插入新的元素
 * 试图向已满的队列中添加新元素的线程将会被阻塞，直到其他线程从队列中移除一个或多个元素或者完全清空，
 * 使队列变得空闲起来并后续新增
 * <p>
 * 好处：
 * 好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都给你一手包办了
 * <p>
 * <p>
 * 队列种类：
 * --ArrayBlockingQueue：由数组结构组成的有界阻塞队列。 （常用）
 * --LinkedBlockingQueue：由链表结构组成的有界（但大小默认值为integer.MAX_VALUE）阻塞队列。（常用）
 * --PriorityBlockingQueue：支持优先级排序的无界阻塞队列。
 * --DelayQueue：使用优先级队列实现的延迟无界阻塞队列。
 * --SynchronousQueue：不存储元素的阻塞队列，也即单个元素的队列。同步队列。（常用）
 * --LinkedTransferQueue：由链表组成的无界阻塞队列。
 * --LinkedBlockingDeque：由链表组成的双向阻塞队列。
 * <p>
 * <p>
 * BlockingQueue核心方法：
 * <p>
 * 1、抛出异常	  当阻塞队列满时，再往队列里add插入元素会抛IllegalStateException:Queue full
 * ---------------当阻塞队列空时，再往队列里remove移除元素会抛NoSuchElementException
 * 2、特殊值	  插入方法，成功ture失败false
 * ---------------移除方法，成功返回出队列的元素，队列里没有就返回null
 * 3、一直阻塞	  当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产者线程直到put数据or响应中断退出
 * ---------------当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程直到队列可用
 * 4、超时退出	  当阻塞队列满时，队列会阻塞生产者线程一定时间，超过限时后生产者线程会退出
 *
 * @author pzl
 * @date 2020-04-04
 */
public class BlockingQueueUtil {

    public static void main(String[] args) throws Exception {

        //创建阻塞队列，初始化容量为3
        BlockingQueue<String> blockingQueue1 = new ArrayBlockingQueue<>(3);
        //创建阻塞队列，初始化容量为3
        BlockingQueue<String> blockingQueue2 = new ArrayBlockingQueue<>(3);
        //创建阻塞队列，初始化容量为3
        BlockingQueue<String> blockingQueue3 = new ArrayBlockingQueue<>(3);
        //创建阻塞队列，初始化容量为3
        BlockingQueue<String> blockingQueue4 = new ArrayBlockingQueue<>(3);

        /*
           1、抛出异常
          阻塞队列满时，再往队列里add插入元素会抛IllegalStateException:Queue full
          当阻塞队列空时，再往队列里remove移除元素会抛NoSuchElementException
         */
        System.out.println(blockingQueue1.add("a")); //true
        System.out.println(blockingQueue1.add("b")); //true
        System.out.println(blockingQueue1.add("c")); //true
        System.out.println(blockingQueue1.element()); //a
        //此时阻塞队列已满，再次添加元素报错
        //System.out.println(blockingQueue1.add("x"));
        System.out.println(blockingQueue1.remove());//a
        System.out.println(blockingQueue1.remove());//b
        System.out.println(blockingQueue1.remove());//c
        //此时阻塞队列空，再次移除元素报错
        //System.out.println(blockingQueue1.remove());
        System.out.println("--------------------以上是抛出异常-----------------------------");

        /*
          2、特殊值
          插入方法，成功ture失败false
          移除方法，成功返回出队列的元素，队列里没有就返回null
         */
        System.out.println(blockingQueue2.offer("a"));//true
        System.out.println(blockingQueue2.offer("b"));//true
        System.out.println(blockingQueue2.offer("c"));//true
        System.out.println(blockingQueue2.offer("x"));//false
        System.out.println(blockingQueue2.poll());//a
        System.out.println(blockingQueue2.poll());//b
        System.out.println(blockingQueue2.poll());//c
        System.out.println(blockingQueue2.poll());//null
        System.out.println("------------------以上是特殊值-------------------------------");

        /*
            3、一直阻塞
            当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产者线程直到put数据or响应中断退出
            当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程直到队列可用
         */
        blockingQueue3.put("a");
        blockingQueue3.put("b");
        blockingQueue3.put("c");
        //blockingQueue3.put("x");//超过容量，就会阻塞。
        System.out.println(blockingQueue3.take());//a
        System.out.println(blockingQueue3.take());//b
        System.out.println(blockingQueue3.take());//c
        //System.out.println(blockingQueue3.take());//阻塞
        System.out.println("----------------------以上是一直阻塞---------------------------");

           /*
              4、超时退出
              当阻塞队列满时，队列会阻塞生产者线程一定时间，超过限时后生产者线程会退出。
            */
        System.out.println(blockingQueue4.offer("a"));//true
        System.out.println(blockingQueue4.offer("b"));//true
        System.out.println(blockingQueue4.offer("c"));//true
        System.out.println(blockingQueue4.offer("a", 3L, TimeUnit.SECONDS));//超时退出
        System.out.println("----------------------以上是超时退出---------------------------");

    }

}
