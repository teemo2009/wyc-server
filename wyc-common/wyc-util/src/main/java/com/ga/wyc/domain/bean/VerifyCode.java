package com.ga.wyc.domain.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VerifyCode {
    private String code;
    private Type type;

    public VerifyCode(String code,Type type){
        this.code=code;
        this.type=type;
    }

    public enum  Type{
        LOGIN
    }

}
