package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.domain.group.IOrderFinishGroup;
import com.ga.wyc.domain.group.IOrderInitGroup;
import com.ga.wyc.domain.group.IOrderTackGroup;
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
    IOrderSerive orderSerive;
    @Resource
    MUtil mUtil;

    @PostMapping("/compute/price")
    public Result computePrice(BigDecimal mile,Integer time){
        return orderSerive.computedPrice(mile,time);
    }

    @PostMapping("/init")
    public Result orderInit(@RequestBody @Validated({IOrderInitGroup.class}) Order order, BindingResult bindingResult){
         mUtil.checkParam(bindingResult);
         //验证坐标
         boolean isLoc=mUtil.isLocationXY(order.getDepLatitude(),order.getDepLongitude(),order.getDestLatitude(),order.getDestLongitude());
         if(!isLoc){
             throw new ValidException("坐标不规范");
         }
         return orderSerive.initOrder(order);
    }

    @PostMapping("/finish")
    public Result orderFinish(@RequestBody @Validated({IOrderFinishGroup.class}) Order order,BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return orderSerive.finishOrder(order);
    }

    /**
     *  客户端 刷新订单
     * */
    @GetMapping("/user/refresh")
    public Result refreshUserOrder(@RequestParam("id") Long id){
        return orderSerive.refreshUserOrder(id);
    }









}
