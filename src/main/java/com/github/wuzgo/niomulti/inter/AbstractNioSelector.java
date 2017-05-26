
package com.github.wuzgo.niomulti.inter;

import com.github.wuzgo.niomulti.pool.NioSelectorRunnablePool;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO 抽象selector线程类
 *
 * @author wuzguo at 2017/5/26 14:31
 */
public abstract class AbstractNioSelector implements Runnable {

    /**
     * 线程池
     */
    private final Executor executor;

    /**
     * 选择器
     */
    protected Selector selector;

    /**
     * 选择器wakenUp状态标记
     */
    private final AtomicBoolean wakenUp = new AtomicBoolean();

    /**
     * 任务队列
     */
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();

    /**
     * 线程名称
     */
    protected String threadName;

    /**
     * 线程管理对象
     */
    private NioSelectorRunnablePool selectorRunnablePool;

    /**
     * @param executor
     * @param threadName
     * @param selectorRunnablePool
     */
    public AbstractNioSelector(final Executor executor, final String threadName, final NioSelectorRunnablePool selectorRunnablePool) {
        this.executor = executor;
        this.threadName = threadName;
        this.selectorRunnablePool = selectorRunnablePool;
        openSelector();
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
     * 获取selector并启动线程
     */
    private void openSelector() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a selector.");
        }

        // 线程池调用Run方法
        executor.execute(this);
    }


    public void run() {
        // 设置线程名称
        Thread.currentThread().setName(this.threadName);

        while (true) {
            try {
                wakenUp.set(false);
                select(selector);
                processTaskQueue();
                process(selector);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 注册一个任务并激活selector
     *
     * @param task
     */
    protected final void registerTask(Runnable task) {
        // 将任务添加到任务队列
        taskQueue.add(task);
        // 选择器
        final Selector selector = this.selector;

        if (selector != null) {
            if (wakenUp.compareAndSet(false, true)) {
                selector.wakeup();
            }
        } else {
            taskQueue.remove(task);
        }
    }

    /**
     * 执行队列里的任务
     */
    private void processTaskQueue() {
        System.out.println(this.getThreadName() + ", processTaskQueue ...");

        for (; ; ) {
            // 将任务从任务队列中弹出
            final Runnable task = taskQueue.poll();
            if (task == null) {
                break;
            }
            // 执行任务
            task.run();
        }
    }

    /**
     * 获取线程管理对象
     *
     * @return
     */
    public NioSelectorRunnablePool getSelectorRunnablePool() {
        return selectorRunnablePool;
    }

    /**
     * select抽象方法
     *
     * @param selector
     * @return
     * @throws IOException
     */
    protected abstract int select(Selector selector) throws IOException;

    /**
     * selector的业务处理
     *
     * @param selector
     * @throws IOException
     */
    protected abstract void process(Selector selector) throws IOException;
}
