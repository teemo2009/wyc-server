package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.domain.group.IDriverAutoLoginGroup;
import com.ga.wyc.domain.group.IDriverLoginGroup;
import com.ga.wyc.domain.vo.DriverLoginVo;
import com.ga.wyc.service.IDriverService;
import com.ga.wyc.util.MUtil;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.groups.Default;
import java.math.BigDecimal;


@RestController
@RequestMapping("/account")
public class AccountController {
    @Resource
    MUtil mUtil;

    @Resource
    IDriverService driverService;

    @PostMapping("/login")
    public Result login(@RequestBody @Validated({IDriverLoginGroup.class,Default.class}) DriverLoginVo vo, BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return driverService.login(vo);
    }

    @PostMapping("/login/auto")
    public Result autoLogin(@RequestBody @Validated({IDriverAutoLoginGroup.class,Default.class}) DriverLoginVo vo, BindingResult bindingResult){
        mUtil.checkParam(bindingResult);
        return driverService.autoLogin(vo);
    }

    @PostMapping("/refresh/token")
    public Result refreshToken(@RequestParam("phone") String phone,
                               @RequestParam("token") String token,
                               @RequestParam("refreshToken") String refreshToken) {
        return driverService.refreshToken(phone, token, refreshToken);
    }


    @PostMapping("/sms/login")
    public Result smsLogin(@RequestParam("phone") String phone){
        return  driverService.sendSmsLogin(phone);
    }


    @PostMapping("/test")
    public Result getOrder(@RequestBody Order order){
        return Result.success().data(order);
    }

}
