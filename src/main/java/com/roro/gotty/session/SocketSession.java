package com.roro.gotty.session;

import com.roro.gotty.base.Server;
import com.roro.gotty.base.Session;

import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author chenqi
 * @date 2021-04-14 11:14
 */
public class SocketSession implements Session {

    private Date creationTime;
    private Date lastAccessedTime;

    private String id;


    private Map<String,Object> attributes;


    private SocketChannel channel;

    private Server applicationContext;



    public SocketSession(SocketChannel channel) {
        Date now=new Date();
        this.channel=channel;
        this.attributes=new HashMap<>();
        this.id= UUID.randomUUID().toString();
        this.creationTime= now;
        this.lastAccessedTime= now;
    }





    public void setLastAccessedTime(Date time){
        this.lastAccessedTime=time;
    }




    @Override
    public long getCreationTime() {
        return this.creationTime!=null?this.creationTime.getTime():0L;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime!=null?this.lastAccessedTime.getTime():0L;
    }

    @Override
    public void setAttribute(String var1, Object var2) {
        attributes.put(var1, var2);
    }

    @Override
    public void removeAttribute(String var1) {
        attributes.remove(var1);
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }
}
