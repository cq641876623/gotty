package com.roro.gotty.base.event;

/**
 * @author chenqi
 * @date 2021-02-05 13:45
 */
public interface EventListener<Data,Source> {
    void handle(Event<Data,Source>event);

}
