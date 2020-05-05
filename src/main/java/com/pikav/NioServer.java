package com.pikav;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 服务器端
 *
 * */

public class NioServer {
    /**
     * 启动
     *
     * */
    public void start() throws IOException {

        /**
         * 1. 创建Selector
         * */
        Selector selector = Selector.open();

        /**
         * 2. 创建ServerSocketChannel创建channel通道
         * */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        /**
         * 3. 为channel通道绑定监听端口
         * */
        serverSocketChannel.bind(new InetSocketAddress(8000));

        /**
         * 4. 设置channel为非阻塞模式
         * */
        serverSocketChannel.configureBlocking(false);

        /**
         * 5. 将channel注册到selector上，监听接事件
         * */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("--------服务器启动成功");
        /**
         * 6. 循环等待新接入的连接
         * */
        for (;;) {  // while(true)
            /**
             * 获取可用channel数量
             * */
            int readyChannels = selector.select();

            /**
             * TODO: 为什么要加这句？
             *
             * */
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
                 * 如果是   接入事件
                 * */
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }
                /**
                 * 如果是   可读事件
                 * */
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }
    }

    /**
     * 接入事件处理器
     * */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        /**
         * 创建socketChannel，与服务端建立连接
         * */
        SocketChannel socketChannel = serverSocketChannel.accept();
        /**
         * 将socketChannel设置为非阻塞工作模式
         * */
        socketChannel.configureBlocking(false);
        /**
         * 将channel注册到selector上，监听 可读事件
         * */
        socketChannel.register(selector, SelectionKey.OP_READ);
        /**
         * 回复客户端提示信息
         * */
        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室其他人都不是朋友关系，请注意隐私安全"));
    }

    /**
     * 可读事件处理器
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
         * 循环读取客户端请求信息
         * */
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) {
            /**
             * 切换buffer为读模式
             * */
            byteBuffer.flip();

            /**
             *  读取buffer中的内容
             * */
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 将channel再次注册到selector上，监听他的可读事件
         * */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 将客户端发送的请求信息  广播给其他客户端
         * */
        if(request.length() > 0) {
            System.out.println(":: " + request);
            broadCast(selector, socketChannel, request);
        }
    }

    /**
     * 广播给其他客户端
     * */
    private void broadCast(Selector selector, SocketChannel sourseChannel, String request) {
        /**
         * 获取到所有已接入等待客户端 channel
         *
         * keys： 返回所有 channel 集合
         * selectedKeys： 返回所有就绪状态的 channel 集合
         * */
        Set<SelectionKey> selectionKeySet = selector.keys();
        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();

            // 剔除发消息的客户端
            if(targetChannel instanceof SocketChannel && targetChannel != sourseChannel) {
                try {
                    // 将消息发送到targetChannel客户端
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * 循环向所有channel广播消息
         * */

    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
