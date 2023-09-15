package com.example.myim.ws.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myim.VO.MessageVO;
import com.example.myim.service.MessageService;
import com.example.myim.utils.EnhancedThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务端处理所有接收消息的handler，这里只是实例，没有拆分太细，建议实际项目中按消息类型拆分到不同的handler中。
 * @author YJL
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebsocketRouterHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final ConcurrentHashMap<Long, Channel> userChannel = new ConcurrentHashMap<>(1000);
    private static final ConcurrentHashMap<Channel, Long> channelUser = new ConcurrentHashMap<>(1000);
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(50,new EnhancedThreadFactory("ackCheckingThreadPool"));
    private static final AttributeKey<AtomicLong> TID_GENERATOR = AttributeKey.valueOf("tid_generator");
    private static final AttributeKey<ConcurrentHashMap> NON_ACKED_MAP = AttributeKey.valueOf("non_acked_map");

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("process error. uid is {},channel is {} info is {}",channelUser.get(ctx.channel()),ctx.channel(),cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[channelClosed]:remote address is {}",ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[channelActive]:remote address is {}",ctx.channel().remoteAddress());
    }

    @Autowired
    private MessageService messageService;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String msg = ((TextWebSocketFrame) frame).text();
            JSONObject msgJson = JSONObject.parseObject(msg);
            int type = msgJson.getIntValue("type");
            JSONObject data = msgJson.getJSONObject("data");
            switch (type) {
                //心跳
                case 0:
                    long uid = data.getLong("uid");
                    Long timeout = data.getLong("timeout");
                    log.info("[heartbeat]: uid = {}, current timeout is {} ms,channel = {}",uid,timeout,ctx.channel());
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":0,\"timeout\":" + timeout + "}"));
                    break;
                //上线消息
                case 1:
                    Long loginUid = data.getLong("uid");
                    userChannel.put(loginUid,ctx.channel());
                    channelUser.put(ctx.channel(), loginUid);
                    ctx.channel().attr(TID_GENERATOR).set(new AtomicLong(0));
                    ctx.channel().attr(NON_ACKED_MAP).set(new ConcurrentHashMap<Long,JSONObject>());
                    log.info("[user bind]: uid = {}, channel = {}",loginUid,ctx.channel());
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":1,\"status\":\"success\"}"));
                    break;
                //查询消息
                case 2:
                    Long ownerUid = data.getLong("ownerUid");
                    Long otherUid = data.getLong("otherUid");
                    List<MessageVO> messageVOS = messageService.queryConversationMsg(ownerUid, otherUid);
                    String msgs = "";
                    if (messageVOS != null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type",2);
                        jsonObject.put("data", JSONArray.toJSON(messageVOS));
                        msgs = jsonObject.toJSONString();
                    }
                    ctx.writeAndFlush(new TextWebSocketFrame(msgs));
                    break;
                //发消息
                case 3:
                    Long senderUid = data.getLong("senderUid");
                    Long recipientUid = data.getLong("recipientUid");
                    String content = data.getString("content");
                    int msgType = data.getIntValue("msgType");
                    MessageVO messageContent = messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
                    if (messageContent != null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type",3);
                        jsonObject.put("data",JSONObject.toJSON(messageContent));
                        ctx.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(jsonObject)));
                    }
                    break;
                //查总未读
                case 5:
                    Long unreadOwnerUid = data.getLong("uid");
                    Long totalUnread = messageService.queryTotalUnread(unreadOwnerUid);
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":5,\"data\":{\"unread\":" + totalUnread + "}}"));
                    break;
                //处理ack
                case 6:
                    Long tid = data.getLong("tid");
                    ConcurrentHashMap<Long,JSONObject> nonAckedMap = ctx.channel().attr(NON_ACKED_MAP).get();
                    nonAckedMap.remove(tid);
                    break;
                default:
                    break;
            }

        }

    }
    public void pushMsg(long recipientUid,JSONObject message) {
        Channel channel = userChannel.get(recipientUid);
        if (channel != null && channel.isActive() && channel.isWritable()) {
            AtomicLong generator = channel.attr(TID_GENERATOR).get();
            long tid = generator.incrementAndGet();
            message.put("tid",tid);
            channel.writeAndFlush(new TextWebSocketFrame(message.toJSONString())).addListener(future -> {
                if (future.isCancelled()) {
                    log.warn("future has been cancelled. {},channel: {}",message,channel);
                }else if (future.isSuccess()){
                    addMsgToAckBuffer(channel,message);
                    log.warn("future has been successfully pushed. {}, channel: {}",message,channel);
                }else {
                    log.error("message write fail, {}, channel: {},cause: {}",message,channel,future.cause());
                }
            });
        }
    }

    /**
     * 将推送的消息加入待ack列表
     * @param channel
     * @param msgJson
     */
    private void addMsgToAckBuffer(Channel channel, JSONObject msgJson) {
        channel.attr(NON_ACKED_MAP).get().put(msgJson.getLong("tid"),msgJson);
        executorService.schedule(()->{
            // 检查是否被 ACK，如果没有收到 ACK 回包，会触发重推
            if (channel.isActive()){
                checkAndResend(channel,msgJson);
            }
        },5000, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查并重推
     * @param channel
     * @param msgJson
     */
    private void checkAndResend(Channel channel, JSONObject msgJson) {
        Long tid = msgJson.getLong("tid");
        int tryTimes = 2;//重推2次
        while (tryTimes > 0){
            if (channel.attr(NON_ACKED_MAP).get().containsKey(tid) && tryTimes > 0){
                channel.writeAndFlush(new TextWebSocketFrame(msgJson.toJSONString()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tryTimes--;
        }
    }

    /**
     * 清除用户和socket映射的相关信息
     * @param channel
     */
    public void cleanUserChannel(Channel channel){
        long uid  = channelUser.remove(channel);
        userChannel.remove(uid);
        log.info("[cleanChannel]:remove uid channel info from gateway,uid is {},channel is {}",uid,channel);
    }
}
