package com.example.myim.mapper;

import com.example.myim.VO.MessageContactVO;
import com.example.myim.domain.ImMsgContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author YJL
 */
@Mapper
public interface MsgContactMapper {

    List<ImMsgContact> findMessageContactsByOwnerUidOrderByMidDesc(@Param("uid") Long uid);

    ImMsgContact findByOwnerUidAndOtherUid(@Param("owneruid")Long ownerUid,@Param("otheruid") Long otherUid);

    int save(ImMsgContact imMsgContact);
}
