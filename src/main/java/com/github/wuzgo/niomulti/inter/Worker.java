
package com.github.wuzgo.niomulti.inter;

import java.nio.channels.SocketChannel;

/**
 * TODO worker接口
 *
 * @author wuzguo at 2017/5/26 14:18
 */
public interface Worker {

    /**
     * 加入一个新的客户端会话
     *
     * @param channel
     */
    void registerNewChannelTask(SocketChannel channel);
}
