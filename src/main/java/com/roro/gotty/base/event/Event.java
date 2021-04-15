package com.roro.gotty.base.event;

import java.util.Date;

/**
 * @author chenqi
 * @date 2021-02-05 13:43
 */
public interface Event<Data,Source>  {

    Data getData();

    Source getSource();

    Date getWhen();

    String getMessage();

    void callBack();

    int getEventType();



}
