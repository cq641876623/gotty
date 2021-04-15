package com.roro.gotty.base;

import java.nio.channels.Channel;

/**
 * @author chenqi
 * @date 2021-04-14 11:10
 */
public interface Session {

    long getCreationTime();

    String getId();

    long getLastAccessedTime();

    void setAttribute(String var1, Object var2);

    void removeAttribute(String var1);


    Channel getChannel();




}
