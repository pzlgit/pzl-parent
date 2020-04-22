package com.pzl.program.juc.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * <p>
 * 一、线程池好处
 * 线程池做的工作只要是控制运行的线程数量，处理过程中将任务放入队列，
 * 然后在线程创建后启动这些任务，如果线程数量超过了最大数量，超出数量的线程排队等候，
 * 等其他线程执行完毕，再从队列中取出任务来执行。
 * <p>
 * 总结：它的主要特点为：线程复用;控制最大并发数;管理线程。
 * <p>
 * 二、线程池简单创建(实际开发中不会使用这几种方式)
 * <p>
 * 1、Executors.newFixedThreadPool(int)
 * 执行长期任务性能好，创建一个线程池， 一池有N个固定的线程，有固定线程数的线程
 * 2、Executors.newSingleThreadExecutor()
 * 一个任务一个任务的执行，一池一线程
 * 3、Executors.newCachedThreadPool()
 * 执行很多短期异步任务，线程池根据需要创建新线程， 但在先前构建的线程可用时将重用它们。可扩容，遇强则强
 * <p>
 * 三、线程池创建重要参数：
 * <p>
 * public ThreadPoolExecutor(int corePoolSize,
 * int maximumPoolSize,
 * long keepAliveTime,
 * TimeUnit unit,
 * BlockingQueue<Runnable> workQueue,
 * ThreadFactory threadFactory,
 * RejectedExecutionHandler handler) {}
 * <p>
 * 1、corePoolSize：线程池中的常驻核心线程数
 * 2、maximumPoolSize：线程池中能够容纳同时 执行的最大线程数，此值必须大于等于1
 * 3、keepAliveTime：多余的空闲线程的存活时间 当前池中线程数量超过corePoolSize时，当空闲时间达到keepAliveTime时，
 * 多余线程会被销毁直到只剩下corePoolSize个线程为止
 * 4、unit：keepAliveTime的单位
 * 5、workQueue：任务队列，被提交但尚未被执行的任务
 * 6、threadFactory：表示生成线程池中工作线程的线程工厂， 用于创建线程，一般默认的即可
 * 7、handler：拒绝策略，表示当队列满了，并且工作线程大于等于线程池的最大线程数（maximumPoolSize）时如何来拒绝 请求执行的runnable的策略
 * <p>
 * 四、线程池底层工作原理（线程池常驻养老区）
 * <p>
 * 1、在创建了线程池后，线程池中的线程数为零。
 * 2、当调用execute()方法添加一个请求任务时，线程池会做出如下判断：
 * --2.1如果正在运行的线程数量小于corePoolSize，那么马上创建线程运行这个任务；
 * --2.2如果正在运行的线程数量大于或等于corePoolSize，那么将这个任务放入队列；
 * --2.3如果这个时候队列满了且正在运行的线程数量还小于maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务；
 * --2.4如果队列满了且正在运行的线程数量大于或等于maximumPoolSize，那么线程池会启动饱和拒绝策略来执行。
 * 3、当一个线程完成任务时，它会从队列中取下一个任务来执行。
 * 4、当一个线程无事可做超过一定的时间（keepAliveTime）时，线程会判断：
 * --如果当前运行的线程数大于corePoolSize，那么这个线程就被停掉。
 * --所以线程池的所有任务完成后，它最终会收缩到corePoolSize的大小。
 * <p>
 * 五、线程池的拒绝策略
 * <p>
 * 使用场景：等待队列已经排满了，再也塞不下新任务了。
 * 同时，线程池中的max线程也达到了，无法继续为新任务服务。
 * 这个是时候我们就需要拒绝策略机制合理的处理这个问题。
 * <p>
 * 1、AbortPolicy(默认)：直接抛出RejectedExecutionException异常阻止系统正常运行
 * 2、CallerRunsPolicy：“调用者运行”一种调节机制，该策略既不会抛弃任务，也不 会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量。
 * 3、DiscardOldestPolicy：抛弃队列中等待最久的任务，然后把当前任务加人队列中 尝试再次提交当前任务。
 * 4、DiscardPolicy：该策略默默地丢弃无法处理的任务，不予任何处理也不抛出异常。 如果允许任务丢失，这是最好的一种策略。
 * <p>
 * 六、线程池的最终创建方式
 * 在工作中单一的/固定数的/可变的三种创建线程池的方法都不用，我们要自己手写线程池。
 * 可能会创建大量线程，会出现OOM内存溢出，我们要通过ThreadPoolExecutor自己创建。
 *
 * @author pzl
 * @date 2020-04-04
 */
public class ThreadPoolUtil {

    public static void main(String[] args) throws Exception {
        ThreadPoolUtil threadPoolUtil = new ThreadPoolUtil();
        //创建固定线程
        ExecutorService executorService1 = threadPoolUtil.createThreadByFixedThreadPool(3);
        for (int i = 1; i < 5; i++) {
            executorService1.execute(() -> System.out.println(Thread.currentThread().getName() + "\t 办理业务"));
        }
        System.out.println("--------------固定线程----------------");
        //创建一池一线程
        ExecutorService executorService2 = threadPoolUtil.createThreadBySingleThreadPool();
        for (int i = 1; i < 7; i++) {
            executorService2.execute(() -> System.out.println(Thread.currentThread().getName() + "\t 办理业务"));
        }
        System.out.println("--------------一池一线程----------------");
        //创建可扩容线程池
        ExecutorService executorService3 = threadPoolUtil.createThreadByCachedThreadPool();
        for (int i = 1; i < 7; i++) {
            executorService3.execute(() -> System.out.println(Thread.currentThread().getName() + "\t 办理业务"));
        }
        System.out.println("--------------可扩容线程池----------------");
    }

    /**
     * 创建固定线程数的线程池
     * <p>
     * newFixedThreadPool创建的线程池corePoolSize和maximumPoolSize值是相等的，
     * 它使用的是LinkedBlockingQueue
     *
     * @param nThreads 线程数
     * @return ExecutorService
     * @throws Exception ex
     */
    public ExecutorService createThreadByFixedThreadPool(int nThreads) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        return executorService;
    }

    /**
     * 创建一个线程，一池一线程
     * <p>
     * newSingleThreadExecutor 创建的线程池corePoolSize和maximumPoolSize值都是1，
     * 它使用的是LinkedBlockingQueue
     *
     * @return ExecutorService
     * @throws Exception ex
     */
    public ExecutorService createThreadBySingleThreadPool() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return executorService;
    }

    /**
     * 线程池根据需要创建新线程,可扩容，遇强则强
     * <p>
     * newCachedThreadPool创建的线程池将corePoolSize设置为0，
     * 将maximumPoolSize设置为Integer.MAX_VALUE，它使用的是SynchronousQueue，
     * 也就是说来了任务就创建线程运行，当线程空闲超过60秒，就销毁线程。
     *
     * @return ExecutorService
     * @throws Exception ex
     */
    public ExecutorService createThreadByCachedThreadPool() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        return executorService;
    }

}
