package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.entity.Order;

import java.math.BigDecimal;
import java.util.List;

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
         *  取消推送的订单
         * */
        Result  driverCancelOrderInit(Long driverCarId);

        /**
         *  接到乘客
         * */
        Result startOrder(Order order);

        /**
         *  到达目的地，司机关单
         * */
        Result reachOrder(Order order);

        /**
         *  支付回调，订单完成
         * */
        Result finishOrder(Order order);
        /**
         *  计算价格
         * */
        Result computedPrice(BigDecimal mile,Integer time);

        /**
         *  客户发单，没有司机接收，取消
         * */
        Result customeCancel(Order order);

        /**
         *  接单后，未接到客人，司机主动取消了订单
         * */
        Result driverCancel(Order order);

        /**
         *  司机端获取所有未接单的 暂不使用
         * */
        Result getOrderList(Double lng,Double lat);

        /**
         *  刷取 司机端 的 接单
         * **/
        Result refreshDriverOrder(Long driverCarId,Long driverCarBatchId);

        /**
         *  刷取 客户端 的 订单,判断是否有司机接单
         */
        Result refreshUserOrder(Long id);

        /**
         *  根据订单查询范围内的司机---发车状态
         * */
        List<DriverCar> getDriverByOrderLngLat(Double lng, Double lat);
}
