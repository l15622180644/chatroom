package com.lzk.chatroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzk.chatroom.entity.Login;
import com.lzk.chatroom.entity.Message;
import com.lzk.chatroom.mapper.MessageMapper;
import com.lzk.chatroom.service.LoginService;
import com.lzk.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Autowired
    private LoginService loginService;

    @Override
    public void sendBM(Message message) {
        message.setCreateTime(System.currentTimeMillis() / 1000);
        message.setType(0);
        Login sender = loginService.getById(message.getSender());
        message.setSenderName(sender.getUserName());
        message.setRead(0);
        save(message);
    }

    @Override
    public void sendPM(Message message) {
        message.setCreateTime(System.currentTimeMillis() / 1000);
        message.setType(1);
        Login sender = loginService.getById(message.getSender());
        message.setSenderName(sender.getUserName());
        Login recipient = loginService.getById(message.getRecipient());
        message.setRecipientName(recipient.getUserName());
        message.setRead(0);
        save(message);
    }

    @Override
    public List<Message> selectByType(Integer type, Integer id) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getRead, type)
                .eq(Message::getRecipient, id)
                .orderByDesc(Message::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Message> selectByHistory(Integer sendId, Integer recipientId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.or(queryWrapper->queryWrapper.eq(Message::getSender,sendId).eq(Message::getRecipient,recipientId))
                .or(queryWrapper->queryWrapper.eq(Message::getSender,recipientId).eq(Message::getRecipient,sendId))
                .orderByAsc(Message::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void modify(List<Message> message) {
        message.forEach(re->re.setRead(1));
        updateBatchById(message);
    }
}
