package com.roro.gotty;

import com.roro.gotty.base.Handler;
import com.roro.gotty.base.Request;
import com.roro.gotty.base.Response;
import com.roro.gotty.base.Session;
import com.roro.gotty.dispatcher.DefaultDispatcher;
import com.roro.gotty.socket.DefaultHandler;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author chenqi
 * @date 2021-04-13 14:23
 */
@Slf4j
public class Main {
    public static void main(String[] args) {



        Gotty gotty=new Gotty();
        gotty.setDispatcher(new DefaultDispatcher());
        int N_CPUS = Runtime.getRuntime().availableProcessors();
        ExecutorService cpu=new ThreadPoolExecutor(N_CPUS, 2 * N_CPUS + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000), new ThreadFactory() {
            int i=0;

            @Override
            public Thread newThread(Runnable r) {
                Thread t=new Thread(r);
                t.setName("CPU-线程池-"+(i++));
                return t;
            }
        });
        ExecutorService io=new ThreadPoolExecutor(N_CPUS, (int) (N_CPUS/(1-0.9)), 60, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1000),new ThreadFactory() {
            int i=0;

            @Override
            public Thread newThread(Runnable r) {
                Thread t=new Thread(r);
                t.setName("IO-线程池-"+(i++));
                return t;
            }
        });
        gotty.setCpuExecutor(io);
        gotty.setIoExecutor(cpu);
        gotty.setHandler(new DefaultHandler());
        gotty.run();


    }
}
