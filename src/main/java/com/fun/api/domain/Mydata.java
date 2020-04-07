package com.fun.api.domain;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Mydata {

    private String msg;
    private String data;
}
