
package com.github.wuzgo.niomulti;

import com.github.wuzgo.niomulti.inter.Keeper;
import com.github.wuzgo.niomulti.pool.NioSelectorRunnablePool;

import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * TODO 服务类
 *
 * @author wuzguo at 2017/5/26 15:23
 */
public class ServerBootstrap {

    private NioSelectorRunnablePool selectorRunnablePool;

    public ServerBootstrap(NioSelectorRunnablePool selectorRunnablePool) {
        this.selectorRunnablePool = selectorRunnablePool;
    }

    /**
     * 绑定端口
     *
     * @param localAddress
     */
    public void bind(final SocketAddress localAddress) {
        try {
            // 获得一个ServerSocket通道
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            // 设置通道为非阻塞
            serverChannel.configureBlocking(false);
            // 将该通道对应的ServerSocket绑定到port端口
            serverChannel.socket().bind(localAddress);

            //获取一个keeper线程
            Keeper nextKeeper = selectorRunnablePool.nextKeeper();
            //向Keeper注册一个ServerSocket通道
            nextKeeper.registerAcceptChannelTask(serverChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
