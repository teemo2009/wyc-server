package com.ga.wyc.domain.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/***
 *  异常基类
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public abstract class BaseException extends RuntimeException {
    private Integer code;
    public BaseException(){}
    public BaseException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code=resultEnum.getCode();
    }
}
