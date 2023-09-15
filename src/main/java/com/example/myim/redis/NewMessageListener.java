package com.example.myim.redis;

import com.alibaba.fastjson.JSONObject;
import com.example.myim.ws.handler.WebsocketRouterHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author YJL
 */
@Component
@Slf4j
public class NewMessageListener implements MessageListener {
    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    private static final RedisSerializer<String> valueSerializer = new GenericToStringSerializer(Object.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = stringRedisSerializer.deserialize(message.getChannel());
        String jsonMsg = valueSerializer.deserialize(message.getBody());
        log.info("Message Received --> pattern: {}. topic:{}, message:{}",new String(pattern),topic,jsonMsg);
        JSONObject msgJSON = JSONObject.parseObject(jsonMsg);
        Long otherUid = msgJSON.getLong("otherUid");
        JSONObject pushJson = new JSONObject();
        pushJson.put("type",4);
        pushJson.put("data",msgJSON);

        websocketRouterHandler.pushMsg(otherUid,pushJson);
    }
}
