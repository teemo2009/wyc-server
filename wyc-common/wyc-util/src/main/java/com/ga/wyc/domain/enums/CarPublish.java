package com.ga.wyc.domain.enums;


/**
 *  是否发车
 * */
public enum  CarPublish implements IntEnum<CarPublish> {
    //发车状态
    START(0),
    //停车状态
    STOP(1);
    private  int value;

    CarPublish(int value){
        this.value=value;
    }

    @Override
    public int getIntValue() {
        return 0;
    }
}
