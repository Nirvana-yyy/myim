package com.example.myim.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName im_msg_contact
 */
public class ImMsgContact implements Serializable {
    /**
     * 
     */
    private Long ownerUid;

    /**
     * 
     */
    private Long otherUid;

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
    private Date createTime;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Long getOwnerUid() {
        return ownerUid;
    }

    /**
     * 
     */
    public void setOwnerUid(Long ownerUid) {
        this.ownerUid = ownerUid;
    }

    /**
     * 
     */
    public Long getOtherUid() {
        return otherUid;
    }

    /**
     * 
     */
    public void setOtherUid(Long otherUid) {
        this.otherUid = otherUid;
    }

    /**
     * 
     */
    public Long getMid() {
        return mid;
    }

    public ImMsgContact(Long ownerUid, Long otherUid, Long mid, Integer type, Date createTime) {
        this.ownerUid = ownerUid;
        this.otherUid = otherUid;
        this.mid = mid;
        this.type = type;
        this.createTime = createTime;
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
    public Integer getType() {
        return type;
    }

    /**
     * 
     */
    public void setType(Integer type) {
        this.type = type;
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
        ImMsgContact other = (ImMsgContact) that;
        return (this.getOwnerUid() == null ? other.getOwnerUid() == null : this.getOwnerUid().equals(other.getOwnerUid()))
            && (this.getOtherUid() == null ? other.getOtherUid() == null : this.getOtherUid().equals(other.getOtherUid()))
            && (this.getMid() == null ? other.getMid() == null : this.getMid().equals(other.getMid()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOwnerUid() == null) ? 0 : getOwnerUid().hashCode());
        result = prime * result + ((getOtherUid() == null) ? 0 : getOtherUid().hashCode());
        result = prime * result + ((getMid() == null) ? 0 : getMid().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ownerUid=").append(ownerUid);
        sb.append(", otherUid=").append(otherUid);
        sb.append(", mid=").append(mid);
        sb.append(", type=").append(type);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}