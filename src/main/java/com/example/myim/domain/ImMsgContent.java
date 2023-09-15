package com.example.myim.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName im_msg_content
 */
public class ImMsgContent implements Serializable {
    /**
     * 
     */
    private Long mid;

    public ImMsgContent(Long mid, String content, Long senderId, Long recipientId, Integer msgType, Date createTime) {
        this.mid = mid;
        this.content = content;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.msgType = msgType;
        this.createTime = createTime;
    }

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Long senderId;

    /**
     * 
     */
    private Long recipientId;

    /**
     * 
     */
    private Integer msgType;

    /**
     * 
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Long getMid() {
        return mid;
    }

    /**
     * 
     */
    public void setMid(Long mid) {
        this.mid = mid;
    }

    /**
     * 
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * 
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * 
     */
    public Long getRecipientId() {
        return recipientId;
    }

    /**
     * 
     */
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * 
     */
    public Integer getMsgType() {
        return msgType;
    }

    /**
     * 
     */
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    /**
     * 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ImMsgContent other = (ImMsgContent) that;
        return (this.getMid() == null ? other.getMid() == null : this.getMid().equals(other.getMid()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getSenderId() == null ? other.getSenderId() == null : this.getSenderId().equals(other.getSenderId()))
            && (this.getRecipientId() == null ? other.getRecipientId() == null : this.getRecipientId().equals(other.getRecipientId()))
            && (this.getMsgType() == null ? other.getMsgType() == null : this.getMsgType().equals(other.getMsgType()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMid() == null) ? 0 : getMid().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getSenderId() == null) ? 0 : getSenderId().hashCode());
        result = prime * result + ((getRecipientId() == null) ? 0 : getRecipientId().hashCode());
        result = prime * result + ((getMsgType() == null) ? 0 : getMsgType().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", mid=").append(mid);
        sb.append(", content=").append(content);
        sb.append(", senderId=").append(senderId);
        sb.append(", recipientId=").append(recipientId);
        sb.append(", msgType=").append(msgType);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}