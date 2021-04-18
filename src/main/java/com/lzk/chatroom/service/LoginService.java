package com.lzk.chatroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzk.chatroom.entity.Login;

import java.util.*;

public interface LoginService extends IService<Login> {

    List<Login> selectAll();

    Login login(Login param);
}
