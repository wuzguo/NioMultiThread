
package com.github.wuzgo.niomulti.internal;

import com.github.wuzgo.niomulti.inter.AbstractNioSelector;
import com.github.wuzgo.niomulti.inter.Keeper;
import com.github.wuzgo.niomulti.inter.Worker;
import com.github.wuzgo.niomulti.pool.NioSelectorRunnablePool;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * TODO Keeper 实现类
 *
 * @author wuzguo at 2017/5/26 15:05
 */
public class NioServerKeeper extends AbstractNioSelector implements Keeper {

    /**
     * @param executor
     * @param threadName
     * @param selectorRunnablePool
     */
    public NioServerKeeper(final Executor executor, final String threadName, final NioSelectorRunnablePool selectorRunnablePool) {
        super(executor, threadName, selectorRunnablePool);
    }

    @Override
    protected void process(final Selector selector) throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (selectedKeys.isEmpty()) {
            return;
        }

        for (Iterator<SelectionKey> iterator = selectedKeys.iterator(); iterator.hasNext(); ) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            // 新客户端
            SocketChannel channel = server.accept();
            // 设置为非阻塞
            channel.configureBlocking(false);
            // 获取一个worker
            Worker nextWorker = this.getSelectorRunnablePool().nextWorker();
            // 注册新客户端接入任务
            nextWorker.registerNewChannelTask(channel);

            System.out.println(this.getThreadName() + ", new client connected...");
        }
    }


    public void registerAcceptChannelTask(final ServerSocketChannel serverChannel) {
        // 获取选择器
        final Selector selector = this.selector;

        this.registerTask(new Runnable() {
            public void run() {
                try {
                    //注册serverChannel到selector
                    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected int select(final Selector selector) throws IOException {
        return selector.select();
    }
}
