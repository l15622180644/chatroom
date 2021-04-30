package com.lzk.chatroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzk.chatroom.entity.Login;
import com.lzk.chatroom.entity.Message;
import com.lzk.chatroom.mapper.LoginMapper;
import com.lzk.chatroom.service.LoginService;
import com.lzk.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class LoginServiceImpl extends ServiceImpl<LoginMapper, Login> implements LoginService {
    @Autowired
    private MessageService messageService;

    @Override
    public List<Login> selectAll(Integer id) {
        LambdaQueryWrapper<Login> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(Login::getId, id);
        List<Login> list = list(wrapper);
        LambdaQueryWrapper<Message> wrapper1 = new LambdaQueryWrapper<>();
        list.forEach(re -> {
            wrapper1.eq(Message::getSender,re.getId())
                    .eq(Message::getRecipient, id)
                    .eq(Message::getRead, 0);
            int count = messageService.count(wrapper1);
            re.setReadCount(count);
            wrapper1.clear();
        });
        return list;
    }

    @Override
    public Login login(Login param) {
        LambdaQueryWrapper<Login> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Login::getLoginName, param.getLoginName())
                .eq(Login::getPassword, param.getPassword());
        return getOne(wrapper);
    }

    @Override
    public List<Login> queryByIds(List<Integer> ids) {
        if (ids.size() == 0) return new ArrayList<>();
        List<Login> logins = listByIds(ids);
        return logins;
    }

    @Override
    public Login selectOne(Integer id) {
        return getById(id);
    }


}
