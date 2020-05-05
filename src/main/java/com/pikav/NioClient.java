package com.pikav;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * NIO 客户端
 *
 * */
public class NioClient {

    /**
     * 启动
     * */
    public void start(String nickname) throws IOException {
        /**
         * 连接服务器端
         * */
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1", 8000));

        System.out.println("-------客户端启动成功");

        /**
         * 接收服务器端响应
         * */
        // 新开一个线程， 专门负责来接收服务器端的响应数据
        // 创建selector， socketChannel， 并且注册socketChannel， 监听可读事件
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();
        System.out.println("-------开始监听服务端响应");

        /**
         * 向服务器端发送数据
         * */
        Scanner scanner = new Scanner(System.in);   //获取键盘输入
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(nickname + " : " + request));
            }
        }
    }

}
