package com.fun.api.domain;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class QrCode implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String event;
    private String type;

}
