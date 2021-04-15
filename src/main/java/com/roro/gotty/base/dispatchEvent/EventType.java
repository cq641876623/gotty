package com.roro.gotty.base.dispatchEvent;

import java.nio.channels.SelectionKey;

/**
 * @author chenqi
 * @date 2021-02-09 13:57
 */
public class EventType {

    public static final int READ= SelectionKey.OP_READ;
    public static final int WRITE= SelectionKey.OP_WRITE;
    public static final int ACCEPT= SelectionKey.OP_ACCEPT;
    public static final int CONNECT= SelectionKey.OP_CONNECT;

}
