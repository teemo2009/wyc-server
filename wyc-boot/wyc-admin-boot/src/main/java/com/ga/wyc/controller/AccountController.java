package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.Manager;
import com.ga.wyc.domain.group.IManagerLoginGroup;
import com.ga.wyc.service.IManagerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    IManagerService managerService;

    @PostMapping("/login")
    public Result login(@RequestBody @Validated({IManagerLoginGroup.class}) Manager manager) {
        return managerService.login(manager);
    }

    @PostMapping("/refresh/token")
    public Result refreshToken(@RequestParam("userName") String userName,@RequestParam("token") String token,
                               @RequestParam("refreshToken") String refreshToken){
        return managerService.refreshToken(userName, token, refreshToken);
    }


}
