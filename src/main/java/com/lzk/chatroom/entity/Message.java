package com.lzk.chatroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String content;
    private Integer sender;
    private String senderName;
    private Integer recipient;
    private String recipientName;
    private Long createTime;
    /**消息类型  0广播消息  1个人消息*/
    private Integer type;
    /** 0未读  1已读*/
    @TableField("`read`")
    private Integer read;
}
