package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.domain.group.*;
import com.ga.wyc.service.IOrderSerive;
import com.ga.wyc.util.MUtil;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    MUtil mUtil;

    @Resource
    IOrderSerive orderSerive;


    /**
     *  计算价格
     * */
    @PostMapping("/compute/price")
    public Result computePrice(BigDecimal mile,Integer time){
        return orderSerive.computedPrice(mile,time);
    }


    /**
     *   司机 刷新订单
     * */
    @GetMapping("/driver/refresh")
    public Result refreshDriverOrder(@RequestParam("driverCarId") Long driverCarId,
                                     @RequestParam("driverCarBatchId") Long driverCarBatchId){
        return orderSerive.refreshDriverOrder(driverCarId,driverCarBatchId);
    }





    /**
     *  司机  取消推送的订单
     * */
    @PostMapping("/init/driver/cancel")
    public Result cancelOrderInit(@RequestParam("driverCarId") Long driverCarId){
        return  orderSerive.driverCancelOrderInit(driverCarId);
    }



    /**
     *  接单
     * */
    @PostMapping("/tack")
    public Result orderTack(@RequestBody @Validated({IOrderTackGroup.class}) Order order, BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return  orderSerive.tackOrder(order);
    }


    /**
     *  确认接客
     * */
    @PostMapping("/start")
    public Result orderStart(@RequestBody @Validated({IOrderStartGroup.class}) Order order, BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return  orderSerive.startOrder(order);
    }

    /**
     *  乘客下车司机端确认
     * */
    @PostMapping("/reach")
    public Result orderReach(@RequestBody @Validated({IOrderReachGroup.class}) Order order, BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return  orderSerive.reachOrder(order);
    }


    /**
     *  司机端 取消订单
     * */
    @PostMapping("/cancel")
    public Result  cancel(@RequestBody @Validated({IOrderDriverCancelGroup.class}) Order order){
        return orderSerive.driverCancel(order);
    }


    /**
     *  根据经度，纬度获取订单，10公里以内
     * */
    @GetMapping("/init/list/get")
    public Result getNearOrderList(@RequestParam("lng") BigDecimal lng, @RequestParam("lat") BigDecimal lat){
        if(!mUtil.isLocationXY(lat,lng)){
            throw new ValidException("坐标不规范");
        }
        return orderSerive.getOrderList(lng.doubleValue(),lat.doubleValue());
    }


}
