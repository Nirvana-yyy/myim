package com.example.myim.mapper;

import com.example.myim.domain.ImUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author YJL
 */
@Mapper
public interface ImUserMapper {

    List<ImUser> findUsersByUidIsNot(@Param("uid") Long uid);

    ImUser findUserByUsername(@Param("usernameBy")String username);

    ImUser findUserByOtherUid(@Param("otherUid")Long otherUid);

    ImUser findUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

}
