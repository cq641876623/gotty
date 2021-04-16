package com.roro.gotty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author chenqi
 * @date 2021-04-16 14:17
 */
public class Test1 {

    public static void main(String[] args) throws IOException {
        Test1 test1=new Test1();
        test1.run();
    }

    ByteBuffer buf = ByteBuffer.allocate(512);

    public void run() throws IOException {
//创建选择器
        Selector selector = Selector.open();
//启动应用程序，监听本机9999端口
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress( 1080));
        serverSocket.configureBlocking(false);
//需要先向selector注册你要监听哪些条件的连接，告诉操作系统凡是需要我处理连接都把连接当前状态记录到selector上
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //轮询selector，select方法会阻塞，直到seletor上有符合条件的连接
            selector.select();
            //如果此次轮询的时候，selector上有多个符合条件的连接，select方法会方法一个列表
            Iterator selectorKeys = selector.selectedKeys().iterator();

            while (selectorKeys.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) selectorKeys.next();
                if (!selectionKey.isValid()) {
                    continue;
                }

                if (selectionKey.isAcceptable()) {
                    accept(selector, selectionKey);
                }

                if (selectionKey.isReadable()) {
                    read(selector, selectionKey);
                }

                if (selectionKey.isWritable()) {
                    selectionKey.cancel();
                    write(selector, selectionKey,":hello Client, I am Server!");
                }
            }
        }

    }


    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        if (clientChannel == null) {
            return;
        }
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(Selector selector, SelectionKey key) throws IOException {
        buf.clear();
        SocketChannel clientChannel = (SocketChannel) key.channel();
        //从通道里面读取数据到缓冲区并返回读取字节数
        int count = clientChannel.read(buf);
        String input = new String(buf.array()).trim();
        System.out.println(Thread.currentThread().getName() + ": Client say " + input);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void write(Selector selector, SelectionKey key, String message) throws IOException {
        buf.clear();
        buf.put(message.getBytes());
        SocketChannel clientChannel = (SocketChannel) key.channel();
        if (clientChannel == null) {
            return;
        }

        int length = 0;
        while ((length = clientChannel.write(buf)) != 0) {
            System.out.println("写入长度:" + length);
        }

        clientChannel.close();
    }

}
