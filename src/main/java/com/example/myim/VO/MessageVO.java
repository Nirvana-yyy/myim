package com.example.myim.VO;

import lombok.Data;

import java.util.Date;

@Data
public class MessageVO {

    private Long mid;
    private String content;
    private Long ownerUid;
    private Integer type;
    private Long otherUid;
    private Date createTime;
    private String ownerUidAvatar;
    private String otherUidAvatar;
    private String ownerName;
    private String otherName;

    public MessageVO(Long mid, String content, Long ownerUid, Integer type, Long otherUid, Date createTime, String ownerUidAvatar, String otherUidAvatar, String ownerName, String otherName) {
        this.mid = mid;
        this.content = content;
        this.ownerUid = ownerUid;
        this.type = type;
        this.otherUid = otherUid;
        this.createTime = createTime;
        this.ownerUidAvatar = ownerUidAvatar;
        this.otherUidAvatar = otherUidAvatar;
        this.ownerName = ownerName;
        this.otherName = otherName;
    }
}
