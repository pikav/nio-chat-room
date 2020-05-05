package com.pikav;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类， 专门接收服务器端响应信息
 * */

public class NioClientHandler implements Runnable{

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            /**
             * 6. 循环等待新接入的连接
             * */
            for (;;) {
                /**
                 * 获取可用channel数量
                 * */
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;
                /**
                 * 获取可用channel集合
                 * */
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();

                Iterator iterator = selectionKeySet.iterator();

                while (iterator.hasNext()) {
                    /**
                     * selectionKey实例
                     * */
                    SelectionKey selectionKey = (SelectionKey) iterator.next();

                    /**
                     * 7. 根据就绪状态，调用对应方法处理业务逻辑
                     * */
                    /**
                     * 移除set中的当前selectionKey
                     * */
                    iterator.remove();
                    /**
                     * 如果是  可读事件
                     * */
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器( 客户端 读取 服务器端 )
     * */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        /**
         * 要从 selectionKey中获取到已经就绪的channel
         * */
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        /**
         * 创建buffer
         * */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        /**
         * 循环读取服务端响应的信息
         * */
        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            /**
             * 切换buffer为读模式
             * */
            byteBuffer.flip();

            /**
             *  读取buffer中的内容
             * */
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 将channel再次注册到selector上，监听他的可读事件
         * */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 将服务端响应信息打印到本地
         * */
        if(response.length() > 0) {
            System.out.println("---来自服务端的信息：" + response);
        }
    }

}
