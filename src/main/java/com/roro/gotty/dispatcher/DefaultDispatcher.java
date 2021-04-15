package com.roro.gotty.dispatcher;

import com.roro.gotty.base.Dispatcher;
import com.roro.gotty.base.dispatchEvent.Event;
import com.roro.gotty.base.dispatchEvent.EventType;
import com.roro.gotty.event.DispatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;

/**
 * @author chenqi
 * @date 2021-04-14 10:50
 */
@Slf4j
public class DefaultDispatcher implements Dispatcher {
    @Override
    public Event dispatch(SelectionKey key) {
        Event event=null;
        if(!key.isValid()){
            key.cancel();
        }else {
            event=new DispatchEvent(key);
        }
        if(key.isValid()&&key.isAcceptable()){
            key.interestOps(key.interestOps() & (~SelectionKey.OP_ACCEPT) );
            event.setEventType(EventType.ACCEPT);
        }
        if(key.isValid()&&key.isConnectable()){
            event.setEventType(EventType.CONNECT);

        }
        if(key.isValid()&&key.isReadable()){
            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ) );
            event.setEventType(EventType.READ);


        }
        if(key.isValid()&&key.isWritable()){
            key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE) );
            event.setEventType(EventType.WRITE);

        }







        return event;
    }
}
