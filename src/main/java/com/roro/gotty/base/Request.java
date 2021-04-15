package com.roro.gotty.base;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenqi
 * @date 2021-04-13 17:44
 */
public interface Request extends Runnable{


    byte[] getBody();

    Session getSession();


    Server getApplicationContext();


    SocketAddress getRemoteAddress();


}
