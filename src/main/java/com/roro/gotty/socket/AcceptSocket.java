package com.roro.gotty.socket;

import com.roro.gotty.base.dispatchEvent.Event;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author chenqi
 * @date 2021-04-14 15:49
 */
@Slf4j
public class AcceptSocket implements Runnable {


    private Event event;


    public AcceptSocket(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) event.getSelectionKey().channel();
            SocketChannel clientChannel = ssc.accept();
            clientChannel.socket().setSoTimeout(3000);
            clientChannel.configureBlocking(false);
            synchronized (event.getSelectionKey().selector().wakeup() ){

                clientChannel.register(event.getSelectionKey().selector(), SelectionKey.OP_READ);
                System.out.println("a new client connected "+clientChannel.getRemoteAddress());
                event.getSelectionKey().interestOps( event.getSelectionKey().interestOps() | (SelectionKey.OP_ACCEPT) );
                event.getSelectionKey().selector().notifyAll();
            }
        } catch (Exception e) {
            log.error("接入socket出错：{}",e);
            e.printStackTrace();
        }

    }
}
