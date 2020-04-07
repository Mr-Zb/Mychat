package com.fun.api.domain;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class options {

    //语音时间
    private String time;
    //视频封面
    private String poster;



}
