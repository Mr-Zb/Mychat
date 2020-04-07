package com.fun.api.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
public class Message<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    //接收者ID
    private String userId;
    //发送者id
    private String fromId;
    //群消息或者私聊消息
    private String fromType;
    //消息类型 image,audio,video，txt
    private String type;
    //消息内容
    private String data;

    private T options;

}
