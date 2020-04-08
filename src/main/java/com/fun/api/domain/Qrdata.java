package com.fun.api.domain;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Qrdata {

    private boolean status;
    private FxGroupInfo group;
}
