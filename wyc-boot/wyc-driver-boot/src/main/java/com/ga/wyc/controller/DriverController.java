package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.group.IRefreshLocation;
import com.ga.wyc.domain.vo.DriverCarRefreshVo;
import com.ga.wyc.service.IDriverService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    /**
     *  获取车辆列表
     * */
    @GetMapping("/car/list/get")
    public Result getCars(@RequestParam("id") Long id){
        return  driverService.getCars(id);
    }

    /**
     *  获取司机当前的发车状态
     * */
    @GetMapping("/car/publish/get")
    public Result getPublishCar(@RequestParam("id") Long id){
        return  driverService.getPublishCar(id);
    }

    /**
     *  发车
     * */
    @PostMapping("/car/start")
    public Result startCars(@RequestParam("driverCarId") Long driverCarId){
        return driverService.startCar(driverCarId);
    }

    /**
     *  收车
     * */
    @PostMapping("/car/stop")
    public Result stopCars(@RequestParam("driverCarId") Long driverCarId){
        return driverService.stopCar(driverCarId);
    }


    /**
     *  坐标刷新
     * */
    @PostMapping("/car/refresh")
    public Result refreshLocation(@RequestBody @Validated({IRefreshLocation.class}) DriverCarRefreshVo driverCar){
        return driverService.refreshLocation(driverCar);
    }
}
