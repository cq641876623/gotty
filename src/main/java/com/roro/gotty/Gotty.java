package com.roro.gotty;

import com.roro.gotty.base.*;
import com.roro.gotty.base.dispatchEvent.Event;
import com.roro.gotty.base.dispatchEvent.EventType;
import com.roro.gotty.session.SocketSession;
import com.roro.gotty.socket.AcceptSocket;
import com.roro.gotty.socket.RequestSocket;
import com.roro.gotty.socket.ResponseSocket;
import com.roro.gotty.utils.Filter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author chenqi
 * @date 2021-04-13 14:26
 */
@Slf4j
public class Gotty implements Server,Runnable{


    private static final int DEFAULT_PORT=1080;
    private int port;

    private ServerSocketChannel server;

    private Selector selector;

    private Dispatcher dispatcher;


    private List<Session> sessionList;


    private ExecutorService ioExecutor;
    private ExecutorService cpuExecutor;


    private Handler handler;

    public Gotty setHandler(Handler handler){
        this.handler=handler;
        return this;
    }



    public Gotty() {
        this(DEFAULT_PORT);
    }

    public Gotty(int port) {
        this.port = port;
        this.sessionList=new ArrayList<>();
        try {
            server= ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(this.port));
            server.configureBlocking(false);
            selector=Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            log.error("nio服务open失败!端口号：{} 异常：{}",port,e);
            e.printStackTrace();
        }
    }


    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }






    @Override
    public void run() {
        launch();
    }

    @Override
    public List<Session> getSessions() {
        return this.sessionList;
    }

    public void setIoExecutor(ExecutorService ioExecutor) {
        this.ioExecutor = ioExecutor;
    }

    public void setCpuExecutor(ExecutorService cpuExecutor) {
        this.cpuExecutor = cpuExecutor;
    }

    @Override
    public void launch() {
        while(! Thread.currentThread().isInterrupted()){
            try{
                int n=selector.select();
                if(n==0){
                    synchronized (selector){
                        selector.wait(1);
                    }
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    Event event=getDispatcher().dispatch(key);

                    switch (event.getEventType()){
                        case EventType.ACCEPT:
                            if(!getIOExecutor().isShutdown())
                            getIOExecutor().submit(new AcceptSocket(event));
                            break;
                        case EventType.READ:
                            Session session=getSession(key.channel());
                            if(session == null){
                                session=new SocketSession((SocketChannel) event.getSelectionKey().channel());
                                this.sessionList.add(session);
                            }
                            if(!getIOExecutor().isShutdown())getIOExecutor().submit(new RequestSocket(event,session,this));
                            break;
                        case EventType.WRITE:
                            if(key.attachment() instanceof Response){
                                Response response= (Response) key.attachment();
                                response.setEvent(event);
                                if(!getIOExecutor().isShutdown())getIOExecutor().submit(response);
                            }
                            break;





                    }










                }

            }catch (Exception e){
                log.error("nio服务open失败!端口号：{} 异常：{}",port,e);
                e.printStackTrace();
                continue;
            }
        }
    }


    private  Session getSession(Channel channel){
        List<Session> resultList=new ArrayList<>();
        for( Session session : sessionList ){
            if(session.getChannel().isOpen()){
                if( channel.equals(session.getChannel())){
                    resultList.add(session);
                }
            }
        }
        return resultList.size()>0?resultList.get(0):null;
    }



    @Override
    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    @Override
    public ExecutorService getIOExecutor() {
        return this.ioExecutor;
    }

    @Override
    public ExecutorService getCPUExecutor() {
        return this.cpuExecutor;
    }

    @Override
    public Handler getHandler() {
        return this.handler;
    }
}
