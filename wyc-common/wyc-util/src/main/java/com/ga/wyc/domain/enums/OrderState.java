package com.ga.wyc.domain.enums;


/**
 *0-完成
 1-客户发起订单，但司机未接单
 2-司机接受订单，司机未接客
 3-司机成功接到客户，开始行程
 4-到达目的，客户支付完成
 5-客户发送订单后，没有司机接单，客户主动取消了订单
 6-接单后，未接到客人，司机主动取消了订单
 7-客户发送订单后，司机已经接单，客户主动取消了订单
 * */
public enum OrderState implements IntEnum{
    FINISH(0),
    INIT(1),
    TAKING(2),
    STARTING(3),
    REACH(4),
    INIT_CANCEL(5),
    DRIVER_CANCEL(6),
    CUSTOME_CANCEL(7);
    private int value;
    OrderState(int value){
        this.value=value;
    }
    @Override
    public int getIntValue() {
        return this.value;
    }
}
