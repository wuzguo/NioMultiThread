
package com.github.wuzgo.niomulti.internal;

import com.github.wuzgo.niomulti.inter.AbstractNioSelector;
import com.github.wuzgo.niomulti.inter.Worker;
import com.github.wuzgo.niomulti.pool.NioSelectorRunnablePool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * TODO worker实现类
 *
 * @author wuzguo at 2017/5/26 14:30
 */
public class NioServerWorker extends AbstractNioSelector implements Worker {

    /**
     * @param executor
     * @param threadName
     * @param selectorRunnablePool
     */
    public NioServerWorker(final Executor executor, final String threadName, final NioSelectorRunnablePool selectorRunnablePool) {
        super(executor, threadName, selectorRunnablePool);
    }

    @Override
    protected void process(final Selector selector) throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            // 移除，防止重复处理
            iterator.remove();

            // 得到事件发生的Socket通道
            SocketChannel channel = (SocketChannel) key.channel();

            // 数据总长度
            int ret = 0;
            boolean failure = true;
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //读取数据
            try {
                ret = channel.read(buffer);
                failure = false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //判断是否连接已断开
            if (ret <= 0 || failure) {
                key.cancel();
                System.out.println(this.getThreadName() + ", Client disconnected... ");
            } else {
                System.out.println(this.getThreadName() + ", Received data :" + new String(buffer.array()));

                //回写数据
                ByteBuffer outBuffer = ByteBuffer.wrap("Hi, Client: I'm Received: ".getBytes());
                // 将消息回送给客户端
                channel.write(outBuffer);
            }
        }
    }

    /**
     * 加入一个新的socket客户端
     */
    public void registerNewChannelTask(final SocketChannel channel) {
        // 获取选择器
        final Selector selector = this.selector;

        // 注册任务
        this.registerTask(new Runnable() {
            public void run() {
                try {
                    //将客户端注册到selector中
                    channel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected int select(final Selector selector) throws IOException {
        return selector.select(500);
    }
}
