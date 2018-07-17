package com.ga.wyc.domain.enums;

public enum NetType implements IntEnum<NetType> {
    LT(1), YD(2), DX(3), NO(4);
    private int value;

    NetType(int value) {
        this.value = value;
    }

    @Override
    public int getIntValue() {
        return this.value;
    }
}