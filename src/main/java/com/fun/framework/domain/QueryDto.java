package com.fun.framework.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel()
public class QueryDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -9032647846361976219L;
    private java.lang.String keyword;
    private java.lang.Integer page = 1;
    private java.lang.Integer rows = 10;
    private java.util.Date beginDate;
    private java.util.Date endDate;

}
