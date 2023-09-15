package com.example.myim.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.myim.VO.MessageContactVO;
import com.example.myim.VO.MessageVO;
import com.example.myim.domain.*;
import com.example.myim.exception.SaveException;
import com.example.myim.mapper.ImUserMapper;
import com.example.myim.mapper.MsgContactMapper;
import com.example.myim.mapper.MsgContentMapper;
import com.example.myim.mapper.MsgRelationMapper;
import com.example.myim.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    ImUserMapper imUserMapper;

    @Autowired
    MsgContentMapper msgContentMapper;

    @Autowired
    MsgRelationMapper msgRelationMapper;

    @Autowired
    MsgContactMapper msgContactMapper;

    @Autowired
    RedisTemplate redisTemplate;

    final String script = new StringBuilder()
            .append("local conv\n")
            .append("conv = redis.call(\"incr\",KEYS[1])\n")
            .append("if tonumber(conv) > 0 then\n")
            .append("   local total\n")
            .append("   total = redis.call(\"hincrby\",KEYS[2],KEYS[3],ARGV[1])\n")
            .append("   if tonumber(total) > 0 then\n")
            .append("       end\n")
            .append("end")
            .toString();

    @Override
    public MessageVO sendNewMsg(long senderUid, long recipientUid, String content, int msgType) {
        Date currentTime = new Date();
        //存内容
        ImMsgContent msgContent = new ImMsgContent(null,content,senderUid,recipientUid,msgType,currentTime);
        int save = msgContentMapper.save(msgContent);
        if (save == 0){
            log.error("保存用户对话失败");
            throw new SaveException("保存用户对话失败");
        }
        Long mid = (long) msgContentMapper.findMidByCurrentTime(currentTime);

        //存发件人的发件箱
        ImMsgRelation messageRelationSender = new ImMsgRelation(senderUid,mid,recipientUid,0,currentTime);
        int save2 = msgRelationMapper.save(messageRelationSender);
        if (save2 == 0){
            log.error("保存用户发件箱失败");
            throw new SaveException("保存用户发件箱失败");
        }

        //存收件人的收件箱
        ImMsgRelation messageRelationRecipient = new ImMsgRelation(recipientUid,mid,senderUid,1,currentTime);
        int save1 = msgRelationMapper.save(messageRelationRecipient);
        if (save1 == 0){
            log.error("保存用户发件箱失败");
            throw new SaveException("保存用户发件箱失败");
        }

        //更新发件人的最近联系人
        ImMsgContact msgContactSender =  msgContactMapper.findByOwnerUidAndOtherUid(senderUid,recipientUid);
        if (msgContactSender != null) {
            msgContactSender.setMid(mid);
        }else{
            msgContactSender = new ImMsgContact(senderUid,recipientUid,mid,0,currentTime);
        }
        int save3 = msgContactMapper.save(msgContactSender);
        if (save3 == 0){
            log.error("保存最近联系人失败");
            throw new SaveException("保存最近联系人失败");
        }

        //更新收件的最近联系人
        ImMsgContact msgContactRecipient =  msgContactMapper.findByOwnerUidAndOtherUid(recipientUid,senderUid);
        if (msgContactRecipient != null) {
            msgContactRecipient.setMid(mid);
        }else{
            msgContactRecipient = new ImMsgContact(recipientUid,senderUid,mid,1,currentTime);
        }
        int save4 = msgContactMapper.save(msgContactRecipient);
        if (save4 == 0){
            log.error("保存最近联系人失败");
            throw new SaveException("保存最近联系人失败");
        }

        //更新读未更新
        //加总未读
        DefaultRedisScript longDefaultRedisScript = new DefaultRedisScript<>();
        longDefaultRedisScript.setScriptSource(new StaticScriptSource(script));
        redisTemplate.execute(longDefaultRedisScript, Arrays.asList(recipientUid +"_T",recipientUid+"_C",String.valueOf(senderUid)),1);
//        redisTemplate.opsForValue().increment(recipientUid + "_T",1);
//        redisTemplate.opsForHash().increment(recipientUid + "_C",senderUid,1);//加会话未读

        //待推送消息发布到redis
        ImUser self = imUserMapper.findUserByOtherUid(senderUid);
        ImUser other = imUserMapper.findUserByOtherUid(recipientUid);
        MessageVO messageVO = new MessageVO(mid,content,self.getUid(),messageRelationSender.getType(),other.getUid(),msgContent.getCreateTime(),self.getAvatar(),other.getAvatar(),self.getUsername(),other.getUsername());
        redisTemplate.convertAndSend("websocket:msg", JSONObject.toJSONString(messageVO));


        return messageVO;
    }

    @Override
    public List<MessageVO> queryConversationMsg(long ownerUid, long otherUid) {
         List<ImMsgRelation> relationList = msgRelationMapper.findAllByOwnerUidAndOtherUidOrderByMidAsc(ownerUid,otherUid);

         return composeMessageVO(relationList,ownerUid,otherUid);
    }

    @Override
    public List<MessageVO> queryNewMsgFrom(Long ownerUid, Long otherUid, Long fromMid) {
        List<ImMsgRelation> relationList =  msgRelationMapper.findAllByOwnerUidAndOtherUidAndBiggerMid(ownerUid,otherUid,fromMid);
        return composeMessageVO(relationList,ownerUid,otherUid);
    }

    @Override
    public MessageContactVO queryContacts(Long ownerUid) {
        List<ImMsgContact> contacts = msgContactMapper.findMessageContactsByOwnerUidOrderByMidDesc(ownerUid);
        if (contacts != null) {
            ImUser user = imUserMapper.findUserByOtherUid(ownerUid);
            long totalUnread = 0;
            Object totalUnreadObj = redisTemplate.opsForValue().get(user.getUid() + "_T");
            if (totalUnreadObj != null){
                totalUnread = Long.parseLong((String) totalUnreadObj);
            }
            MessageContactVO contactVO = new MessageContactVO(user.getUid(),user.getAvatar(), user.getUsername(), totalUnread);
            contacts.forEach(contact->{
                Long mid = contact.getMid();
                ImMsgContent contentVO = msgContentMapper.findByMid(mid);
                ImUser otherUser = imUserMapper.findUserByOtherUid(contact.getOtherUid());
                if (contactVO != null) {
                    long convUnread = 0;
                    Object convUnreadObj = redisTemplate.opsForHash().get(user.getUid() + "_C", otherUser.getUid());
                    if (convUnreadObj != null) {
                        convUnread = Long.parseLong((String) convUnreadObj);
                    }
                    MessageContactVO.ContactInfo contactInfo = contactVO.new ContactInfo(otherUser.getUid(), otherUser.getUsername(), otherUser.getAvatar(),mid,contact.getType(),contentVO.getContent(),convUnread,contact.getCreateTime());
                    contactVO.appendContact(contactInfo);
                }
            });
            return contactVO;
        }
        return null;
    }

    @Override
    public Long queryTotalUnread(Long unreadOwnerUid) {
        long totalUnread = 0;
        Object totalUnreadObj = redisTemplate.opsForValue().get(unreadOwnerUid + "_T");
        if (totalUnreadObj != null) {
            totalUnread = Long.parseLong((String) totalUnreadObj);
        }
        return totalUnread;
    }

    private List<MessageVO> composeMessageVO(List<ImMsgRelation> relationList,long ownerUid,long otherUid){
        if (null != relationList && !relationList.isEmpty()){
            //先拼接消息索引和消息内容
            List<MessageVO> msgList = new ArrayList<>();
            ImUser self = imUserMapper.findUserByOtherUid(ownerUid);
            ImUser other = imUserMapper.findUserByOtherUid(otherUid);
            relationList.forEach((relation)->{
                Long mid = relation.getMid();
                ImMsgContent contentVO = msgContentMapper.findByMid(mid);
                if (null != contentVO){
                    String content = contentVO.getContent();
                    MessageVO messageVO = new MessageVO(mid, content,relation.getOwnerUid(),relation.getType(),relation.getOtherUid(),relation.getCreateTime(),self.getAvatar(),other.getAvatar(),self.getUsername(),other.getUsername());
                    msgList.add(messageVO);
                }
            });

            //再变更未读
            Object convUnreadObj = redisTemplate.opsForHash().get(ownerUid + "_C",otherUid);
            if (convUnreadObj != null){
                long convUnread = Long.parseLong((String) convUnreadObj);
                redisTemplate.opsForHash().delete(ownerUid+"_C",otherUid);
                Long afterCleanUnread = redisTemplate.opsForValue().increment(ownerUid + "_T", -convUnread);
                //修正总未读
                if (afterCleanUnread <= 0){
                    redisTemplate.delete(ownerUid + "_T");
                }
            }
            return msgList;
        }
        return null;
    }
}
