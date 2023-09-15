package com.example.myim.service;

import com.example.myim.VO.MessageContactVO;
import org.springframework.stereotype.Service;
import com.example.myim.domain.ImUser;

import java.util.List;

/**
 * @author YJL
 */

public interface UserService {

    ImUser login(String username,String password);

    List<ImUser> getAllUsersExcept(Long uid);

    MessageContactVO getContacts(ImUser ownerUser);

}
