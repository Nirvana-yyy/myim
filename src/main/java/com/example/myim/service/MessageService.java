package com.example.myim.service;

import com.example.myim.VO.MessageContactVO;
import com.example.myim.VO.MessageVO;

import java.util.List;

public interface MessageService {

    MessageVO sendNewMsg(long senderUid,long recipientUid,String content,int msgType);

    List<MessageVO> queryConversationMsg(long ownerUid, long otherUid);

    List<MessageVO> queryNewMsgFrom(Long ownerUid, Long otherUid, Long fromMid);

    MessageContactVO queryContacts(Long ownerUid);

    Long queryTotalUnread(Long unreadOwnerUid);
}
