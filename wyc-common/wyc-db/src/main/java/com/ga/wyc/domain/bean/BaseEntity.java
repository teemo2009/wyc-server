package com.ga.wyc.domain.bean;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity implements Serializable {
}
