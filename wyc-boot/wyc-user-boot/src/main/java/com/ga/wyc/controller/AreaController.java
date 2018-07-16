package com.ga.wyc.controller;

import com.ga.wyc.service.IAreaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/area")
public class AreaController {


    @Resource
    IAreaService areaService;

    @GetMapping(value = "/init")
    public String init(){
        areaService.initArea();
        return "ok";
    }


}
