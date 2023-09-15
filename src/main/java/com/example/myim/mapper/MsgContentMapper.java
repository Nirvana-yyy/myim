package com.example.myim.mapper;

import com.example.myim.domain.ImMsgContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author YJL
 */
@Mapper
public interface MsgContentMapper {

    ImMsgContent findByMid(@Param("mid") Long mid);

    int save(ImMsgContent imMsgContent);

    int findMidByCurrentTime(@Param("currentTime") Date currentTime);
}
