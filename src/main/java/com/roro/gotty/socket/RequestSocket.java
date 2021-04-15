package com.roro.gotty.socket;

import com.roro.gotty.base.*;
import com.roro.gotty.base.dispatchEvent.Event;
import com.roro.gotty.base.dispatchEvent.EventType;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author chenqi
 * @date 2021-04-14 10:26
 */
@Slf4j
public class RequestSocket implements Request {


    private Event event;


    private Session session;

    private byte[] body;

    private Server applicationContext;







    public RequestSocket(Event event, Session session,Server applicationContext) {
        this.event = event;
        this.session = session;
        this.applicationContext=applicationContext;
    }

    @Override
    public byte[] getBody() {
        return this.body;
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
    public SocketAddress getRemoteAddress() {
        SocketChannel socketChannel= (SocketChannel) event.getSelectionKey().channel();
        return socketChannel.socket().getRemoteSocketAddress();
    }


    private void processingIO(){
        if( event != null && event.getEventType() == EventType.READ ){
            SelectionKey key = event.getSelectionKey();
            SocketChannel channel= (SocketChannel) key.channel();
            ByteBuffer buf=ByteBuffer.allocate(1024);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            int len;
            try{
                while ((len=channel.read(buf))>0){
                    buf.flip();
                    baos.write(buf.array(), 0, len);
                    buf.clear();
                }
                this.body=baos.toByteArray();
                baos.close();
                if (len == -1) {
                    channel.close();
                    key.cancel();
                }
            }catch (Exception e){
                log.error("地址：{} 构造请求异常：{}",channel.socket().getRemoteSocketAddress(),e);
                e.printStackTrace();
            }
        }else {
            log.error("请求无效:event:{}",event);
        }
    }


    @Override
    public void run() {
        this.processingIO();
        if(this.applicationContext.getHandler()!=null && !this.applicationContext.getCPUExecutor().isShutdown()){
            ResponseSocket responseSocket=new ResponseSocket(null,session,applicationContext);
            try {
                Constructor constructor=this.applicationContext.getHandler().getClass().getConstructor();
                Handler handler= (Handler) constructor.newInstance();
                handler.setRequest(this);
                handler.setResponse(responseSocket);
                handler.setKey(event.getSelectionKey());
                this.applicationContext.getCPUExecutor().submit(handler);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }





        }

    }
}
