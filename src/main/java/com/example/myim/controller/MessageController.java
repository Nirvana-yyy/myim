package com.example.myim.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myim.VO.MessageContactVO;
import com.example.myim.VO.MessageVO;
import com.example.myim.exception.SaveException;
import com.example.myim.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YJL
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendMsg")
    @ResponseBody
    public String sendMsg(@RequestParam Long senderUid, @RequestParam Long recipientUid, String content, Integer msgType){
        try{
            MessageVO messageVO = messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
            if (null != messageVO){
                return JSONObject.toJSONString(messageVO);
            }else {
                return " ";
            }
        }catch (SaveException e){
            e.printStackTrace();
            return " ";
        }

    }

    @GetMapping("/queryMsg")
    @ResponseBody
    public String queryMsg(@RequestParam Long ownerUid,@RequestParam Long otherUid ){
        List<MessageVO> messageVO = messageService.queryConversationMsg(ownerUid,otherUid);
        if (messageVO != null){
            return JSONObject.toJSONString(messageVO);
        } else{
            return " ";
        }
    }

    @GetMapping("/queryMsgSinceMid")
    @ResponseBody
    public String queryMsgSinceMid(@RequestParam Long ownerUid, @RequestParam Long otherUid,@RequestParam Long lastMid){
        List<MessageVO> messageVOS = messageService.queryNewMsgFrom(ownerUid, otherUid,lastMid);
        if (messageVOS != null) {
            return JSONArray.toJSONString(messageVOS);
        } else {
            return "";
        }
    }

    @GetMapping("queryContacts")
    @ResponseBody
    public String queryContacts(@RequestParam Long ownerUid ){
        MessageContactVO contactVO = messageService.queryContacts(ownerUid);
        if (contactVO != null) {
            return JSONObject.toJSONString(contactVO);
        }else{
            return "";
        }
    }
}
