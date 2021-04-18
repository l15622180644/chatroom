package com.lzk.chatroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzk.chatroom.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {

    /**
     * 发送广播消息
     */
    void sendBM(Message message);

    /**
     * 发送私人消息
     */
    void sendPM(Message message);

    List<Message> selectByType(Integer type,Integer id);
}
