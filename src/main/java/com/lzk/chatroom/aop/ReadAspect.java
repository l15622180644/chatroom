package com.lzk.chatroom.aop;

import com.lzk.chatroom.controller.MessageController;
import com.lzk.chatroom.entity.Message;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author
 * @module
 * @date 2021/4/29 10:37
 */
@Aspect
@Component
public class ReadAspect {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageController messageController;

    @Pointcut("@annotation(com.lzk.chatroom.annotation.UpdateRead)")
    public void pointcut() {
    }

//    @AfterReturning(value = "pointcut()")
//    public void afterReturning() throws InterruptedException {
//        List<Message> list = MessageController.list;
//        if (list.size() > 0) {
//            messageController.updateMsg(list);
//            list.clear();
//        }
//        log.info("aop:{}", "修改消息状态执行完成");
//    }
}
