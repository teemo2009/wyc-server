package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.service.IDriverService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Resource
    IDriverService driverService;

    @GetMapping("/info/get")
    public Result getInfo(@RequestParam("id") Long id){
        return driverService.getInfo(id);
    }
}
