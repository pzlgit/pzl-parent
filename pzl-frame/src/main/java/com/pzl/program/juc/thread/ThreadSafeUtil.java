package com.pzl.program.juc.thread;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 解决 List、map、set 线程安全问题
 * <p>
 * 一、集合类是不安全的：List、Map、Set 没有synchronized，线程不安全
 * <p>
 * java.util.ConcurrentModificationException
 * ArrayList在迭代的时候如果同时对其进行修改就会
 * 抛出java.util.ConcurrentModificationException异常
 * 并发修改异常。
 * <p>
 * 二、解决方案：
 * 1、Vector  线程安全，有synchronized，但是版本太低，jdk1.0就有了
 * 2、Collections（效率低）  List<String> list = Collections.synchronizedList(new ArrayList<>());
 * 3、写时复制 （推荐）
 * List<String> list = new CopyOnWriteArrayList<>();//线程安全
 * Set<String> set = new CopyOnWriteArraySet<>();//线程安全
 * Map<String,String> map = new ConcurrentHashMap<>();//线程安全
 * 其中所有可变操作（add、set等）都是通过生成底层数组的新副本来实现的。
 * <p>
 * 写时复制理论：
 * CopyOnWrite容器即写时复制的容器。往一个容器添加元素的时候，不直接往当前容器Object[]添加，
 * 而是先将当前容器Object[]进行Copy，复制出一个新的容器Object[] newElements，然后向新的容器Object[] newElements里添加元素。
 * 添加元素后，再将原容器的引用指向新的容器setArray(newElements)。
 * 这样做的好处是可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。
 * 所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器。
 *
 * @author pzl
 * @date 2020-04-04
 */
public class ThreadSafeUtil {

    public static void main(String[] args) {
        ThreadSafeUtil threadSafeUtil = new ThreadSafeUtil();
        threadSafeUtil.safeDemo();
    }

    /**
     * 集合类安全demo
     */
    public void safeDemo() {
        List list = new CopyOnWriteArrayList();
        Set set = new CopyOnWriteArraySet();
        Map<String, Object> map = new ConcurrentHashMap<>();
        //开启多线程set数据到结构中
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(1, 8));
                System.out.println(list);

                //Set的底层就是Map key=set值，value=new Object();
                set.add(UUID.randomUUID().toString().substring(1, 8));
                System.out.println(set);

                map.put(UUID.randomUUID().toString().substring(1, 8), new Object());
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }

    /**
     * 集合类不安全demo
     * java.util.ConcurrentModificationException :并发修改异常
     */
    public void notSafeDemo() {
        List list = new ArrayList();
        Set set = new HashSet();
        Map<String, Object> map = new HashMap<>();
        //开启多线程set数据到结构中
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(1, 8));
                System.out.println(list);

                //Set的底层就是Map key=set值，value=new Object();
                set.add(UUID.randomUUID().toString().substring(1, 8));
                System.out.println(set);

                map.put(UUID.randomUUID().toString().substring(1, 8), new Object());
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }

}
