package com.ga.wyc.domain.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Data
@Accessors(fluent = true)
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result(){

    }
    private Result(ResultEnum resultEnum){
        this.code=resultEnum.getCode();
        this.message=resultEnum.getMessage();
    }

    public static Result success(){
        return new Result(ResultEnum.SUCCESS);
    }

    public static Result fail(){
        return new Result(ResultEnum.FAIL);
    }
    public static Result unkonw(){
        return new Result(ResultEnum.UNKONW_ERROR);
    }

    public static Result custom(){
        return new Result();
    }
}
