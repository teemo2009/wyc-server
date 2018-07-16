package com.ga.wyc.domain.bean;

public enum ResultEnum {
    //未知错误 code可以自定义
    UNKONW_ERROR(-1,"未知错误"),
    //成功
    SUCCESS(0,"成功"),
    //逻辑业务失败
    FAIL(1,"失败"),
    //参数验证错误
    PARAM_VALID_ERROR(101,"参数验证错误");

    private Integer code;
    private String message;

     ResultEnum(Integer code, String message){
        this.code=code;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }

}
