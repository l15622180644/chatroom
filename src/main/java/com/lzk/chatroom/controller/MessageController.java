package com.lzk.chatroom.controller;

import com.lzk.chatroom.entity.Message;
import com.lzk.chatroom.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageService messageService;

    @PostMapping("/sendBM")
    public void sendBM(@RequestBody Message message) {
        messageService.sendBM(message);
        messagingTemplate.convertAndSend("/broadcast/sendMsg", message.getId());
    }

    @PostMapping("/sendPM")
    public void sendPM(@RequestBody Message message){
        messageService.sendPM(message);
        messagingTemplate.convertAndSendToUser(message.getRecipient()+"","/alone/msg",message.getId());
    }

    @PostMapping("/notRead")
    public List<Message> notRead(@RequestBody Integer id){
        return messageService.selectByType(0,id);
    }

}
