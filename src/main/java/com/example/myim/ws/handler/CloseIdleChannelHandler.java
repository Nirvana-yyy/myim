package com.example.myim.ws.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 若干个心跳周期内没有消息的收发，关闭连接。
 * @author YJL
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class CloseIdleChannelHandler extends ChannelDuplexHandler {

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("connector no receive ping packet from client, will close. channel: {}",ctx.channel());
                websocketRouterHandler.cleanUserChannel(ctx.channel());
                ctx.close();
            }
        }
    }



}
