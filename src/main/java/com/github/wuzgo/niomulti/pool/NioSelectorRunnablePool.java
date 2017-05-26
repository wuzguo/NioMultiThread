
package com.github.wuzgo.niomulti.pool;

import com.github.wuzgo.niomulti.inter.Keeper;
import com.github.wuzgo.niomulti.inter.Worker;
import com.github.wuzgo.niomulti.internal.NioServerKeeper;
import com.github.wuzgo.niomulti.internal.NioServerWorker;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO selector线程管理者
 *
 * @author wuzguo at 2017/5/26 14:21
 */
public class NioSelectorRunnablePool {

    // Keeper原子自增序号
    private final AtomicInteger keeperIndex = new AtomicInteger();

    // Keeper线程数组
    private Keeper[] keepers;

    // Keeper线程池
    private Executor keeperExecutor;

    // Worker原子自增序号
    private final AtomicInteger workerIndex = new AtomicInteger();

    // Worker线程数组
    private Worker[] workers;

    // Worker线程池
    private Executor workerExecutor;

    public NioSelectorRunnablePool(final Executor keeperExecutor, final Executor workerExecutor) {
        this.keeperExecutor = keeperExecutor;
        this.workerExecutor = workerExecutor;

        initKeeper(this.keeperExecutor, 1);

        // Returns the number of processors available to the Java virtual machine
        initWorker(this.workerExecutor, Runtime.getRuntime().availableProcessors() * 2);
    }


    /**
     * 初始化 keeper 线程
     *
     * @param keeperExecutor
     * @param count
     */
    private void initKeeper(final Executor keeperExecutor, final int count) {
        this.keepers = new NioServerKeeper[count];
        for (int i = 0; i < keepers.length; i++) {
            keepers[i] = new NioServerKeeper(keeperExecutor, "keeperExecutor thread " + (i + 1), this);
        }
    }

    /**
     * 初始化worker线程
     *
     * @param workerExecutor
     * @param count
     */
    private void initWorker(final Executor workerExecutor, final int count) {
        this.workers = new NioServerWorker[count];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new NioServerWorker(workerExecutor, "workerExecutor thread " + (i + 1), this);
        }
    }

    /**
     * 获取一个worker
     *
     * @return
     */
    public Worker nextWorker() {
        int index = Math.abs(workerIndex.getAndIncrement() % workers.length);
        System.out.println("index: " + index);
        return workers[index];

    }

    /**
     * 获取一个keeper
     *
     * @return
     */
    public Keeper nextKeeper() {
        return keepers[Math.abs(keeperIndex.getAndIncrement() % keepers.length)];
    }

}
