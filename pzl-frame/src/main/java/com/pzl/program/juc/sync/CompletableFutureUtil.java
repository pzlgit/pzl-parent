package com.pzl.program.juc.sync;

import java.util.concurrent.CompletableFuture;

/**
 * 异步回调
 *
 * @author pzl
 * @date 2020-04-05
 */
public class CompletableFutureUtil {

    public static void main(String[] args) throws Exception {
        //同步调用，无返回值
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t completableFuture1");
        });
        completableFuture1.get();

        //异步回调，有返回值
        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t completableFuture2");
            //int i = 10 / 0;
            return 1024;
        });

        completableFuture2.whenComplete((t, u) -> {
            System.out.println("-------t=" + t);//返回值 1024
            System.out.println("-------u=" + u);//当方法运行错误的时候的错误信息
        }).exceptionally(f -> {
            System.out.println("-----exception:" + f.getMessage());
            return 444;
        }).get();
    }

}
