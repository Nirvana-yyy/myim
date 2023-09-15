package com.example.myim.controller;

import com.example.myim.VO.MessageContactVO;
import com.example.myim.domain.ImUser;
import com.example.myim.exception.InvalidUserInfoException;
import com.example.myim.exception.UserNotExistException;
import com.example.myim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author YJL
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String welcomePage(HttpSession session){
        if (session.getAttribute("userLogin") != null) {
            return "index";
        }else{
            return "login";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        try{
            ImUser user = userService.login(email, password);
            model.addAttribute("loginUser",user);
            session.setAttribute("user",user);
            //获取该用户联系人，因为重点不在这，所以简化，直接就获取剩下的用户了
            List<ImUser> otherUser = userService.getAllUsersExcept(user.getUid());
            model.addAttribute("otherUsers",otherUser);
            MessageContactVO contactVO = userService.getContacts(user);
            model.addAttribute("contactVO",contactVO);
        }catch (UserNotExistException e){
            model.addAttribute("errorMsg",email+"用户信息不存在");
            return "login";
        }catch (InvalidUserInfoException e){
            model.addAttribute("errorMsg",email+"密码错误");
            return "login";
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("userLogin");
        return "redirect:/";
    }

    @RequestMapping("/ws")
    public String ws(Model model,HttpSession session){
        ImUser loginUser = (ImUser)session.getAttribute("user");
        model.addAttribute("loginUser",loginUser);
        List<ImUser> otherUsers = userService.getAllUsersExcept(loginUser.getUid());
        model.addAttribute("otherUsers",otherUsers);
        MessageContactVO contactVO = userService.getContacts(loginUser);
        model.addAttribute("contactVO", contactVO);
        return "index_ws";
    }

}
