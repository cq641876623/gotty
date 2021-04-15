package com.roro.gotty.base;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * @author chenqi
 * @date 2021-04-15 10:13
 */
@Slf4j
public abstract class AbstractHandler implements Handler{

    protected Request request;

    protected Response response;

    protected SelectionKey key;

    @Override
    public SelectionKey getKey() {
        return key;
    }
    @Override
    public void setKey(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void setRequest(Request request) {
        this.request=request;
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
    }


    @Override
    public Request getRequest() {
        return this.request;
    }


    @Override
    public Response getResponse() {
        return this.response;
    }

    @Override
    public void run() {
        if(this.handler()){
            this.handlerCallBack(getResponse());
        }else {
            if(getKey()!=null){
                getKey().interestOps( getKey().interestOps() | SelectionKey.OP_READ );
                getKey().selector().wakeup();
            }
        }

    }


    @Override
    public void handlerCallBack(Response response) {
        if(getKey()!=null){
            getKey().attach(response);
            response.transformResponseBuffer();
            getKey().interestOps( getKey().interestOps() | SelectionKey.OP_WRITE );
            getKey().selector().wakeup();
        }else {
            log.error("handler中key为null");
        }
    }
}
