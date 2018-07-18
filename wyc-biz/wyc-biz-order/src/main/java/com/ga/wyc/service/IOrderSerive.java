package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.Order;

import java.math.BigDecimal;

public interface IOrderSerive {
        /**
         *   初始化发起订单
         * */
        Result initOrder(Order order);

        /**
         *  司机接单
         * */
        Result   tackOrder(Order order);

        /**
         *  接到乘客
         * */
        Result startOrder(Order order);

        /**
         *  到达目的地，乘客支付完成
         * */
        Result reachOrder(Order order);

        /**
         *  司机关单
         * */
        Result finishOrder(Order order);

        /**
         *  客户发单，没有司机接收，取消
         * */
        Result initCancel(Order order);

        /**
         *  接单后，未接到客人，司机主动取消了订单
         * */
        Result driverCancel(Order order);

        /**
         *  客户发送订单后，司机已经接单，客户主动取消了订单
         * */
        Result customCancel(Order order);

        /**
         *  司机端获取所有未接单的
         * */
        Result getOrderList(Double lng,Double lat);
}
