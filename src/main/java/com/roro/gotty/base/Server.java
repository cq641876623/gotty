package com.roro.gotty.base;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author chenqi
 * @date 2021-04-14 8:51
 */
public interface Server {

    List<Session> getSessions();
    /**
     * 实现Reactor
     */
    void launch();


    Dispatcher getDispatcher();


    ExecutorService getIOExecutor();

    ExecutorService getCPUExecutor();



    Handler getHandler();



}
