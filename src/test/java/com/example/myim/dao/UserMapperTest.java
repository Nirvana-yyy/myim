package com.example.myim.dao;

import com.example.myim.MyimApplication;
import com.example.myim.domain.ImUser;
import com.example.myim.mapper.ImUserMapper;
import com.example.myim.mapper.MsgContactMapper;
import com.example.myim.mapper.MsgContentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MyimApplication.class)
public class UserMapperTest {

    @Autowired
    ImUserMapper userMapper;

    @Autowired
    MsgContentMapper msgContentMapper;

    @Autowired
    MsgContactMapper msgContactMapper;

    @Test
    public void test1(){
//        ImUser user = userMapper.findUserByUsername("zhangsan@gmail.com");
//        ImUser userByOtherUid = userMapper.findUserByOtherUid(1000L);
//        System.out.println(user);
//        msgContentMapper.findByMid(1000L);
        System.out.println(msgContactMapper.findMessageContactsByOwnerUidOrderByMidDesc(1000L));
    }

}
