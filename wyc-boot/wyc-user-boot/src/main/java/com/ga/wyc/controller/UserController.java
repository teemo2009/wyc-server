package com.ga.wyc.controller;

import com.ga.wyc.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;

    @GetMapping("/test")
    public String test(){
        return "";
    }



}
