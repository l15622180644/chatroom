package com.lzk.chatroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("login")
public class Login implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String userName;
    private String loginName;
    private String password;
}
