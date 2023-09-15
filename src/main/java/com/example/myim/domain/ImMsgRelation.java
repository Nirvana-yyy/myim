package com.example.myim.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName im_msg_relation
 */
@Data
public class ImMsgRelation implements Serializable {
    /**
     * 
     */
    private Long ownerUid;

    /**
     * 
     */
    private Long mid;

    /**
     * 
     */
    private Long otherUid;

    /**
     * 
     */
    private Integer type;

    /**
     * 
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public ImMsgRelation(Long ownerUid, Long mid, Long otherUid, Integer type, Date createTime) {

        this.ownerUid = ownerUid;
        this.mid = mid;
        this.otherUid = otherUid;
        this.type = type;
        this.createTime = createTime;
    }


}