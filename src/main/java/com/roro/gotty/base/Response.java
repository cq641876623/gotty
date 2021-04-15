package com.roro.gotty.base;

import com.roro.gotty.base.dispatchEvent.Event;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author chenqi
 * @date 2021-04-14 11:06
 */
public interface Response extends Runnable{


    ByteBuffer getResponseBuffer();


    void transformResponseBuffer();

    ByteArrayOutputStream getBody();

    Session getSession();

    SocketAddress getRemoteAddress();

    Server getApplicationContext();

    boolean getEnd();
    void setEnd(boolean end);

    void setEvent(Event event);





}
