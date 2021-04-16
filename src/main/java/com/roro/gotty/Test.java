package com.roro.gotty;

import com.roro.gotty.base.dispatchEvent.EventType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenqi
 * @date 2021-04-15 9:08
 */
public class Test {

    public static Integer p=new Integer(0);

    public static volatile boolean[] lockp;


    public static void main(String[] args) throws IOException {

        ServerSocketChannel server= ServerSocketChannel.open();

        server.socket().bind(new InetSocketAddress(1080));
        server.configureBlocking(false);
        Selector acceptSelector=Selector.open();
        int n=4;
        Selector[] selectors=new Selector[n];
        lockp=new boolean[n];
        server.register(acceptSelector, SelectionKey.OP_ACCEPT);
        CountDownLatch count=new CountDownLatch(n);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(! Thread.currentThread().isInterrupted()){
                    try{
                        count.await();
                        acceptSelector.select();
                        Iterator<SelectionKey> it = acceptSelector.selectedKeys().iterator();
                        while(it.hasNext()) {
                            SelectionKey key = it.next();
                            it.remove();
                            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                            SocketChannel clientChannel = ssc.accept();
                            clientChannel.socket().setSoTimeout(3000);
                            clientChannel.configureBlocking(false);
                            synchronized (p){
                                p++;
                                if(p>=n){
                                    p=0;
                                }
                                selectors[p].wakeup();
                                lockp[p]=true;
                                clientChannel.register(selectors[p],SelectionKey.OP_READ);
                                System.out.println("a new client connected \t"+p+"\t"+clientChannel.getRemoteAddress());
                                lockp[p]=false;

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        for(int i=0;i<n;i++){
            selectors[i]=Selector.open();
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(! Thread.currentThread().isInterrupted()){
                        try{
                            if(lockp[finalI]){
                                continue;
                            }
                            count.countDown();
                            selectors[finalI].select();
                            Iterator<SelectionKey> it = selectors[finalI].selectedKeys().iterator();
                            while(it.hasNext()) {
                                SelectionKey key = it.next();
                                it.remove();
                                if(key.isValid()&&key.isReadable()){
                                    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ) );
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ByteBuffer buf = ByteBuffer.allocate(512);
                                            SocketChannel clientChannel = (SocketChannel) key.channel();
                                            //从通道里面读取数据到缓冲区并返回读取字节数
                                            try {
                                                int count = clientChannel.read(buf);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            String input = new String(buf.array()).trim();
                                            System.out.println(Thread.currentThread().getName() + ": Client say " + input);
                                            key.interestOps(SelectionKey.OP_WRITE);
                                        }
                                    }).start();


                                }
                                if(key.isValid()&&key.isWritable()){
                                    key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE) );
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ByteBuffer buf = ByteBuffer.allocate(512);
                                            buf.put("收到".getBytes());
                                            buf.flip();
                                            SocketChannel clientChannel = (SocketChannel) key.channel();
                                            if (clientChannel == null) {
                                                return;
                                            }

                                            int length = 0;
                                            while (true) {
                                                try {
                                                    if (!((length = clientChannel.write(buf)) != 0)) break;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                System.out.println("写入长度:" + length);
                                            }

                                            try {
                                                clientChannel.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                }





                            }




                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }




                }
            }).start();


        }



















    }
}
