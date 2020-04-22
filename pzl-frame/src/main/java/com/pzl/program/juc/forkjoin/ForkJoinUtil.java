package com.pzl.program.juc.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 分支合并框架
 * <p>
 * Fork/Join框架是Java7提供的一个用于并行执行任务的框架，是一个把大任务分割成若干个小任务，
 * 最终汇总每个小任务结果后得到大任务结果的框架。Fork/Join框架要完成两件事情：
 * <p>
 * 1.任务分割：首先Fork/Join框架需要把大的任务分割成足够小的子任务，如果子任务比较大的话还要对子任务进行继续分割。
 * <p>
 * 2.执行任务并合并结果：分割的子任务分别放到双端队列里，然后几个启动线程分别从双端队列里获取任务执行。
 * 子任务执行完的结果都放在另外一个队列里，启动一个线程从队列里取数据，然后合并这些数据。
 * <p>
 * 分支框架使用：
 * 一、 创建Task
 * 使用ForkJoin框架，需要创建一个ForkJoin的任务，而ForkJoinTask是一个抽象类，我们不需要去继承ForkJoinTask进行使用。
 * 因为ForkJoin框架为我们提供了RecursiveAction和RecursiveTask。
 * 我们只需要继承ForkJoin为我们提供的抽象类的其中一个并且实现compute方法。
 * <p>
 * 二、使用ForkJoinPool进行执行
 * task要通过ForkJoinPool来执行，分割的子任务也会添加到当前工作线程的双端队列中，
 * 进入队列的头部。当一个工作线程中没有任务时，会从其他工作线程的队列尾部获取一个任务(工作窃取)。
 * <p>
 * 三、RecursiveTask和RecursiveAction区别
 * RecursiveTask
 * 通过源码的查看我们可以发现RecursiveTask在进行exec之后会使用一个result的变量进行接受返回的结果。
 * 而result返回结果类型是通过泛型进行传入。也就是说RecursiveTask执行后是有返回结果。
 * <p>
 * RecursiveAction
 * RecursiveAction在exec后是不会保存返回结果，因此RecursiveAction与RecursiveTask区别在与
 * RecursiveTask是有返回结果而RecursiveAction是没有返回结果。
 *
 * @author pzl
 * @date 2020-04-05
 */
public class ForkJoinUtil {

    public static void main(String[] args) {
        int[] array = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        SumTask sumTask = new SumTask(0, array.length - 1, array);

        long start = System.currentTimeMillis();

        forkJoinPool.invoke(sumTask);
        System.out.println("The count is " + sumTask.join()
                + " spend time:" + (System.currentTimeMillis() - start) + "ms");
    }

}

class SumTask extends RecursiveTask<Integer> {

    private int threshold;
    //设定数组的长度大于10才进行任务切割
    private static final int segmentation = 10;

    private int[] src;

    private int fromIndex;
    private int toIndex;

    public SumTask(int formIndex, int toIndex, int[] src) {
        this.fromIndex = formIndex;
        this.toIndex = toIndex;
        this.src = src;
        this.threshold = src.length / segmentation;
    }

    @Override
    protected Integer compute() {
        if ((toIndex - fromIndex) < threshold) {
            int count = 0;
            System.out.println(" from index = " + fromIndex + " toIndex=" + toIndex);
            for (int i = fromIndex; i <= toIndex; i++) {
                count += src[i];
            }
            return count;
        } else {
            int mid = (fromIndex + toIndex) / 2;
            SumTask left = new SumTask(fromIndex, mid, src);
            SumTask right = new SumTask(mid + 1, toIndex, src);
            invokeAll(left, right);
            return left.join() + right.join();
        }
    }

}
