package com.roro.gotty.base;

import com.roro.gotty.base.dispatchEvent.Event;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

/**
 * @author chenqi
 * @date 2021-04-13 17:22
 */
public interface Dispatcher {




    Event dispatch(SelectionKey key);






}
