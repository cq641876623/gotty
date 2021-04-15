package com.roro.gotty.base;

import java.nio.channels.SelectionKey;

/**
 * @author chenqi
 * @date 2021-04-14 17:13
 */
public interface Handler extends Runnable{


    void setRequest(Request request);
    void setResponse(Response response);


    SelectionKey getKey();
    void  setKey(SelectionKey key);


    Request getRequest();
    Response getResponse();

    boolean handler();


    void handlerCallBack(Response response);
}
