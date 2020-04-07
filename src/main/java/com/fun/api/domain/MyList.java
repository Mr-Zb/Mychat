package com.fun.api.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MyList implements Serializable {
    private static final long serialVersionUID = 1L;

    private String letter;
    private List<FxFriends> data;

}
