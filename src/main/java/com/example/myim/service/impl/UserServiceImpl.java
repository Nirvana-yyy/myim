package com.example.myim.service.impl;

import com.example.myim.VO.MessageContactVO;
import com.example.myim.domain.ImMsgContact;
import com.example.myim.domain.ImMsgContent;
import com.example.myim.exception.InvalidUserInfoException;
import com.example.myim.exception.UserNotExistException;
import com.example.myim.mapper.ImUserMapper;
import com.example.myim.mapper.MsgContactMapper;
import com.example.myim.mapper.MsgContentMapper;

import com.example.myim.service.UserService;
import com.example.myim.domain.ImUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ImUserMapper userMapper;

    @Autowired
    private MsgContactMapper msgContactMapper;

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public ImUser login(String username, String password) {
        ImUser user = userMapper.findUserByUsername(username);
        if (user == null) {
            log.warn("用户不存在"+username);
            throw new UserNotExistException("用户信息不存在"+username);
        }else{
            user = userMapper.findUserByUsernameAndPassword(username, password);
            if (user == null){
                log.info(username+"密码错误");
                throw new InvalidUserInfoException("无效用户信息");
            }else{
                log.info(username+"登录成功");
            }
        }

        return user;
    }

    @Override
    public List<ImUser> getAllUsersExcept(Long uid) {
        List<ImUser> usersByUidIsNot = userMapper.findUsersByUidIsNot(uid);

        return usersByUidIsNot;

    }

    @Override
    public MessageContactVO getContacts(ImUser ownerUser) {
        List<ImMsgContact> contacts = msgContactMapper.findMessageContactsByOwnerUidOrderByMidDesc(ownerUser.getUid());
        if (contacts != null ||contacts.size() != 0){
            Long totalUnread = 0L;
            Object totalUnreadObj = redisTemplate.opsForValue().get(ownerUser.getUid() + "_T");
            if (totalUnreadObj != null) {
                totalUnread = Long.parseLong((String) totalUnreadObj);
            }
            MessageContactVO messageContactVO = new MessageContactVO(ownerUser.getUid(), ownerUser.getAvatar(), ownerUser.getUsername(), totalUnread);
            contacts.forEach(contact->{
                Long mid = contact.getMid();
                ImMsgContent contentVO = msgContentMapper.findByMid(contact.getMid());
                ImUser otherUser = userMapper.findUserByOtherUid(contact.getOtherUid());
                if (contentVO != null){
                    long convUnread = 0;
                    Object convUnreadObj = redisTemplate.opsForHash().get(ownerUser.getUid()+"_C",otherUser.getUid());
                    if (convUnreadObj != null) {
                        convUnread = Long.parseLong((String) convUnreadObj);
                    }
                    MessageContactVO.ContactInfo contactInfo = messageContactVO.new ContactInfo(otherUser.getUid(), otherUser.getUsername(), otherUser.getAvatar(), mid, contact.getType(), contentVO.getContent(), convUnread, contact.getCreateTime());
                    messageContactVO.appendContact(contactInfo);
                }
            });
            return messageContactVO;

        }
        return null;
    }
}
