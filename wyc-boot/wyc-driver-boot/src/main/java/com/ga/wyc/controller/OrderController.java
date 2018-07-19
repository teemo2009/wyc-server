package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.entity.Order;
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
    MUtil mUtil;

    @Resource
    IOrderSerive orderSerive;




    /**
     *  接单
     * */
    @PostMapping("/tack")
    public Result orderTack(@RequestBody @Validated({IOrderTackGroup.class}) Order order, BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return  orderSerive.tackOrder(order);
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
