package com.lzk.chatroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzk.chatroom.entity.Login;
import com.lzk.chatroom.mapper.LoginMapper;
import com.lzk.chatroom.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper,Login> implements LoginService{
    @Override
    public List<Login> selectAll() {
        return list();
    }

    @Override
    public Login login(Login param) {
        LambdaQueryWrapper<Login> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Login::getLoginName,param.getLoginName())
                .eq(Login::getPassword,param.getPassword());
        return getOne(wrapper);
    }
}
