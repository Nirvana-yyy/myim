package com.example.myim.mapper;

import com.example.myim.domain.ImMsgRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MsgRelationMapper {

    int save(ImMsgRelation imMsgRelation);

    List<ImMsgRelation> findAllByOwnerUidAndOtherUidOrderByMidAsc(@Param("ownerUid") long ownerUid, @Param("otherUid") long otherUid);

    List<ImMsgRelation> findAllByOwnerUidAndOtherUidAndBiggerMid(@Param("ownerUid") Long ownerUid, @Param("otherUid") Long otherUid, @Param("fromMid") Long fromMid);
}
