package com.roro.gotty.socket;

import com.roro.gotty.base.Response;
import com.roro.gotty.base.Server;
import com.roro.gotty.base.Session;
import com.roro.gotty.base.dispatchEvent.Event;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author chenqi
 * @date 2021-04-15 10:03
 */
@Slf4j
public class ResponseSocket implements Response {


    private ByteBuffer buf;


    private ByteArrayOutputStream baos;

    private Event event;


    private Session session;


    private Server applicationContext;

    private boolean end=false;




    public ResponseSocket(Event event, Session session, Server applicationContext) {
        this.event = event;
        this.session = session;
        this.applicationContext = applicationContext;
        this.baos=new ByteArrayOutputStream();
    }

    @Override
    public ByteBuffer getResponseBuffer() {
        return this.buf;
    }

    @Override
    public void transformResponseBuffer() {
        this.buf=ByteBuffer.wrap(this.baos.toByteArray());
    }


    @Override
    public ByteArrayOutputStream getBody() {
        return this.baos;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public Server getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public boolean getEnd() {
        return end;
    }


    @Override
    public void setEvent(Event event) {
        this.event=event;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        SocketChannel socketChannel= (SocketChannel) event.getSelectionKey().channel();
        return socketChannel.socket().getRemoteSocketAddress();
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    @Override
    public void run() {
        SocketChannel socketChannel= (SocketChannel) event.getSelectionKey().channel();
        if(getResponseBuffer().hasRemaining() ){
            try {
                socketChannel.write(getResponseBuffer());
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getSelectionKey().interestOps( event.getSelectionKey().interestOps() | SelectionKey.OP_WRITE );
            event.getSelectionKey().selector().wakeup();
        }else {
            log.info("响应\t{} 信息：{}",getRemoteAddress(),new String(getBody().toByteArray()));
            if(getEnd()){
                try {
                    socketChannel.close();
                    event.getSelectionKey().cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                event.getSelectionKey().interestOps( event.getSelectionKey().interestOps() | SelectionKey.OP_READ );
                event.getSelectionKey().selector().wakeup();
            }
        }
    }
}
