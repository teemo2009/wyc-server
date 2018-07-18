package com.ga.wyc.domain.enums;

/**
 *
 * 0:未结算
 1:已结算
 2:未知
 */
public enum PayState implements IntEnum<PayState> {
    UNPAY(0),
    PAYED(1),
    UNKONW(2);

    private int value;

    PayState(int value){
        this.value=value;
    }

    @Override
    public int getIntValue() {
        return this.value;
    }
}
