package com.lzk.chatroom.controller;

import com.alibaba.fastjson.JSONArray;
import com.lzk.chatroom.entity.Login;
import com.lzk.chatroom.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LoginService loginService;
    @Autowired
    private MessageController messageController;
    private static List<Integer> loginIds = new ArrayList<>();

    @GetMapping("/getAll")
    public List<Login> getAll(Integer id){
        List<Login> logins = loginService.selectAll(id);
        for(Login login : logins){
            if(loginIds.contains(login.getId())){
                login.setType(0);
            }
        }
        return logins;
    }

    @PostMapping("/login")
    public Login login(@RequestBody Login login){
        Login login1 = loginService.login(login);
        if(login1==null){
            log.info("登录失败");
            return null;
        }
        loginIds.add(login1.getId());
        login1.setType(0);
        messageController.sendLoginMsg(login1);
        return login1;
    }

    @GetMapping("/logout")
    public String logout(Integer id){
        Login login = loginService.selectOne(id);
        login.setType(1);
        messageController.sendLoginMsg(login);
        return "已退出";
    }

    @GetMapping("/getLoginUser")
    public List getLoginUser(){
        List<Integer> loginIds = new ArrayList<>();
        List<Login> logins = loginService.queryByIds(loginIds);
        return logins;
    }
}
