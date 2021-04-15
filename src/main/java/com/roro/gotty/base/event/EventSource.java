package com.roro.gotty.base.event;

/**
 * @author chenqi
 * @date 2021-02-05 13:45
 */
public interface EventSource {

    void addListener(EventListener listenter);

    void notifyAllListenter(Event event);

}
