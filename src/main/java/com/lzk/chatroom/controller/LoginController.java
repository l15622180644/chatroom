package com.lzk.chatroom.controller;

import com.lzk.chatroom.entity.Login;
import com.lzk.chatroom.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LoginService loginService;

    @GetMapping("/getAll")
    public List<Login> getAll(){
        return loginService.selectAll();
    }

    @PostMapping("/login")
    public Login login(@RequestBody Login login){
        Login login1 = loginService.login(login);
        if(login1==null){
            log.info("登录失败");
            return null;
        }
        return login1;
    }
}
