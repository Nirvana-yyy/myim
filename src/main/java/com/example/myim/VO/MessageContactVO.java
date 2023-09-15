package com.example.myim.VO;


import lombok.Data;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author YJL
 */
@Data
public class MessageContactVO {

    /**
     *
     */
    private Long ownerUid;

    /**
     *
     */
    private String ownerAvatar;

    /**
     *
     */
    private String ownerName;

    /**
     *
     */
    private Long totalUnread;

    /**
     *
     */
    private List<ContactInfo> contactInfoList;

    public MessageContactVO(Long ownerUid, String ownerAvatar, String ownerName, Long totalUnread) {

        this.ownerUid = ownerUid;
        this.ownerAvatar = ownerAvatar;
        this.ownerName = ownerName;
        this.totalUnread = totalUnread;
    }

    public void appendContact(ContactInfo contactInfo) {
        if (contactInfoList != null) {
        } else {
            contactInfoList = new ArrayList<>();
        }
        contactInfoList.add(contactInfo);
    }

    @Data
    public class ContactInfo {
        /**
         *
         */
        private Long otherUid;

        /**
         *
         */
        private String otherName;

        /**
         *
         */
        private String otherAvatar;

        /**
         *
         */
        private Long mid;

        /**
         *
         */
        private Integer type;

        /**
         *
         */
        private String content;

        /**
         *
         */
        private Long convUnread;

        private Date createTime;


        public ContactInfo(Long uid, String username, String avatar, Long mid, Integer type, String content, long convUnread, Date createTime) {
            this.otherUid = uid;
            this.otherName = username;
            this.otherAvatar = avatar;
            this.mid = mid;
            this.type = type;
            this.content = content;
            this.convUnread = convUnread;
            this.createTime = createTime;
        }
    }
}
