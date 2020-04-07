package com.fun.api.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ImMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID 唯一ID
     */
    private String id;
    /**
     *发送者头像
     */
    private String from_avatar;
    /**
     * 发送者昵称
     */
    private String from_name;
    /**
     * 发送者ID
     */
    private String from_id;
    /**
     * 接受者ID/群ID
     */
    private String to_id;
    /**
     * 接受者 接受者人/群 名称
     */
    private String to_name;
    /**
     *接受者人/群 头像
     */
    private String to_avatar;
    /**
     * 接受类型 user group
     */
    private String chat_type;
    /**
     * 消息类型 image,audio,video，txt
     */
    private String type;
    /**
     * 消息内容
     */
    private String data;

    /**
     * 其他参数
     */
    private options options;
    /**
     * 创建时间
     */
    private Long create_time;
    /**
     * 是否撤回
     */
    private String is_remove;



}
