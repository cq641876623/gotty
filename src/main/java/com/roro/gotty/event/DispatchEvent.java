package com.roro.gotty.event;

import com.roro.gotty.base.dispatchEvent.Event;

import java.nio.channels.SelectionKey;

/**
 * @author chenqi
 * @date 2021-04-14 10:06
 */
public class DispatchEvent implements Event {


    private int eventType;

    private SelectionKey key;


    public DispatchEvent( SelectionKey key) {
        this.key = key;
    }


    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    public int getEventType() {
        return this.eventType;
    }

    @Override
    public SelectionKey getSelectionKey() {
        return this.key;
    }
}
