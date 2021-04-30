package com.lzk.chatroom.controller;

import com.lzk.chatroom.annotation.UpdateRead;
import com.lzk.chatroom.entity.Login;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class MessageController {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageService messageService;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    //发送广播消息
    @PostMapping("/sendBM")
    public void sendBM(@RequestBody Message message) {
        messageService.sendBM(message);
        messagingTemplate.convertAndSend("/broadcast/sendMsg", message.getId());
    }

    //发送个人聊天消息
    @PostMapping("/sendPM")
    public Message sendPM(@RequestBody Message message) {
        messageService.sendPM(message);
        messagingTemplate.convertAndSendToUser(message.getRecipient() + "", "/alone/msg", message);
        return message;
    }

    @PostMapping("/notRead")
    public List<Message> notRead(@RequestBody Integer id) {
        return messageService.selectByType(0, id);
    }

    @PostMapping("/sendLoginMsg")
    public void sendLoginMsg(Login login) {
        messagingTemplate.convertAndSend("/broadcast/loginMsg", login);
    }

    /**
     * 获取聊天记录
     *
     * @param sendId      当前请求账号
     * @param recipientId 当前请求账号的聊天对象
     * @return
     */
    @GetMapping("/getHistory")
    public List<Message> getHistory(Integer sendId, Integer recipientId) throws InterruptedException {
        List<Message> list = null;
        List<Message> history = messageService.selectByHistory(sendId, recipientId);
        for (Message re : history) {
            if (re.getSender().intValue() == recipientId.intValue() && re.getRead() == 0) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                Message message = new Message();
                message.setId(re.getId());
                message.setRead(1);
                message.setSender(re.getSender());
                list.add(message);
            }
        }
        List<Message> list1 = list;
        if (list != null) {
            threadPool.submit(() -> {
                log.info("进入线程");
                this.updateMsg(list1);
            });
        }
        return history;
    }

    //修改已读、未读状态
    @PostMapping("/updateMsg")
    public void updateMsg(@RequestBody List<Message> message) {
        messageService.modify(message);
        messagingTemplate.convertAndSendToUser(message.get(0).getSender() + "", "/alone/read", message);
    }

}
