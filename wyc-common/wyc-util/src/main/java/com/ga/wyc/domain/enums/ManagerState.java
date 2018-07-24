package com.ga.wyc.domain.enums;

public enum ManagerState implements   IntEnum<ManagerState> {
    ABLE(0), //禁用
    DISABLE(1);//启用


    private int value;

    ManagerState(int value){
        this.value=value;
    }

    @Override
    public int getIntValue() {
        return this.value;
    }
}
