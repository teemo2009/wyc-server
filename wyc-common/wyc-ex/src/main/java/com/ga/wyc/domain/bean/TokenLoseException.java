package com.ga.wyc.domain.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *  token 失效异常
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class TokenLoseException extends  BaseException {
        public TokenLoseException(){
            super(ResultEnum.TOKEN_LOSE_ERROR);
        }

}
