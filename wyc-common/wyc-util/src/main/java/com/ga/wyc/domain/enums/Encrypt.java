package com.ga.wyc.domain.enums;
/**
 *  1:GCJ-02 高德
 2:WGS84
 3:BD-百度
 4:CGCS-北斗标准
 * */
public enum Encrypt implements IntEnum<Encrypt> {
    GCJ(1),WGS(2),BD(3),CGCS(4);
    private int value;
    Encrypt(int value){
        this.value=value;
    }

    @Override
    public int getIntValue() {
        return this.value;
    }
}
