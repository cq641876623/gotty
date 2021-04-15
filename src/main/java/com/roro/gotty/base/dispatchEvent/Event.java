package com.roro.gotty.base.dispatchEvent;

import java.nio.channels.SelectionKey;

/**
 * @author chenqi
 * @date 2021-04-14 9:58
 */
public interface Event {

    int getEventType();


    SelectionKey getSelectionKey();

    void setEventType(int i);

}
