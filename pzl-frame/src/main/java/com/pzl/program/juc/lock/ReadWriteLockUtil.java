package com.pzl.program.juc.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁 工具类
 * <p>
 * 写锁：独占锁
 * 读锁:共享锁
 * <p>
 * 读写数据的时候都会发生死锁,我们可以通过读写锁避免死锁。
 *
 * @author pzl
 * @date 2020-04-04
 */
public class ReadWriteLockUtil {
    //创建易变的map
    private volatile Map<String, Object> map = new HashMap<>();
    //读写锁
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        //写锁上锁
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在写" + key);
            //暂停一会儿线程
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "\t 写完了" + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //写锁解锁
            rwLock.writeLock().unlock();
        }
    }

    public Object get(String key) {
        //读锁上锁
        rwLock.readLock().lock();
        Object result = null;
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在读" + key);
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = map.get(key);
            System.out.println(Thread.currentThread().getName() + "\t 读完了" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //写锁解锁
            rwLock.readLock().unlock();
        }
        return result;
    }

}

class ReadWriteLockDemo {

    public static void main(String[] args) {
        ReadWriteLockUtil readWriteLockUtil = new ReadWriteLockUtil();
        //写数据
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            new Thread(() -> {
                readWriteLockUtil.put(num + "", num + "");
            }, String.valueOf(i)).start();
        }
        //读数据
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            new Thread(() -> {
                readWriteLockUtil.get(num + "");
            }, String.valueOf(i)).start();
        }
    }

}
