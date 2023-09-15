package com.example.myim.dao;

import com.example.myim.MyimApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.StaticScriptSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(classes = MyimApplication.class)
public class LuaTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void test(){
        String script = new StringBuilder()
                .append("local conv\n")
                .append("conv = redis.call(\"incr\",KEYS[1])\n")
                .append("if tonumber(conv) > 0 then\n")
                .append("   local total\n")
                .append("   total = redis.call(\"hincrby\",KEYS[2],KEYS[3],ARGV[1])\n")
                .append("   if tonumber(total) > 0 then\n")
                .append("       end\n")
                .append("end")
                .toString();
        DefaultRedisScript longDefaultRedisScript = new DefaultRedisScript<>();

        longDefaultRedisScript.setScriptSource(new StaticScriptSource(script));
        redisTemplate.execute(longDefaultRedisScript, Arrays.asList("lua1","htest","key1"),1);

    }

}
