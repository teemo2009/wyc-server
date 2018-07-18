package com.ga.wyc.domain.enums;

public enum DriverCarState implements   IntEnum<DriverCarState> {
    //正在接客
    RUNNING(0),
    //闲置
    IDLE(1);
    private int value;

    DriverCarState(int value){
        this.value=value;
    }

    @Override
    public int getIntValue() {
        return this.value;
    }
}
