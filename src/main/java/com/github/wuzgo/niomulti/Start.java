
package com.github.wuzgo.niomulti;

import com.github.wuzgo.niomulti.pool.NioSelectorRunnablePool;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * TODO 启动类
 *
 * @author wuzguo at 2017/5/26 15:25
 */
public class Start {

    public static void main(String[] args) {
        //初始化线程池
        NioSelectorRunnablePool nioSelectorRunnablePool = new NioSelectorRunnablePool(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        //获取服务类
        ServerBootstrap bootstrap = new ServerBootstrap(nioSelectorRunnablePool);

        //绑定端口
        bootstrap.bind(new InetSocketAddress(10100));

        System.out.println("server start...");
    }
}
