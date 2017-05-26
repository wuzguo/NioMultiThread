
package com.github.wuzgo.niomulti.inter;

import java.nio.channels.ServerSocketChannel;

/**
 * TODO Keeper 接口
 *
 * @author wuzguo at 2017/5/26 14:17
 */
public interface Keeper {

    /**
     * 加入ServerSocket
     *
     * @param serverChannel
     */
    void registerAcceptChannelTask(ServerSocketChannel serverChannel);
}
