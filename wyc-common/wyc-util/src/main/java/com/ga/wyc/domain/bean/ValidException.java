package com.ga.wyc.domain.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 *  参数验证异常
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ValidException extends  BaseException {
    private String detail;
    public ValidException(String detail) {
        super(ResultEnum.PARAM_VALID_ERROR);
        this.detail=detail;
    }
}
