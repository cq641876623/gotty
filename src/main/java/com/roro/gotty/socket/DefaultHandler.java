package com.roro.gotty.socket;

import com.roro.gotty.base.AbstractHandler;
import com.roro.gotty.base.Handler;
import com.roro.gotty.base.Request;
import com.roro.gotty.base.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author chenqi
 * @date 2021-04-15 9:53
 */
@Slf4j
public class DefaultHandler extends AbstractHandler {




    @Override
    public boolean handler() {
        log.info("收到 \t{}\t信息:{}",getRequest().getRemoteAddress(),new String(getRequest().getBody()));
        try {
            getResponse().getBody().write(("revice \t"
                    +getRequest().getRemoteAddress()
                    +"\tmsg:\t"
                    +new String(getRequest().getBody())).getBytes()
            );
            getResponse().setEnd(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }


}
