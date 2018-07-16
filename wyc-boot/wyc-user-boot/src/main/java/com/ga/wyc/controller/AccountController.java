package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    IUserService userService;

    @PostMapping("/sms/login")
    public Result smsLogin(@RequestParam("phone") String phone) {
        return userService.sendSmsLogin(phone);
    }

    @PostMapping("/refresh/token")
    public Result refreshToken(@RequestParam("phone") String phone,
                               @RequestParam("token") String token,
                               @RequestParam("refreshToken") String refreshToken) {
        return userService.refreshToken(phone, token, refreshToken);
    }

    @PostMapping("/login")
    public Result login(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        return userService.login(phone, code);
    }

}