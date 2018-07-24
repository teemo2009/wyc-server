package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.entity.User;
import com.ga.wyc.service.IDriverService;
import com.ga.wyc.service.IUserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;

    @Resource
    IDriverService driverService;

    /**
     *  用户更新信息
     * */
    @PostMapping("/info/update")
    public Result updateInfo(@RequestBody @Valid User user, BindingResult bindingResult){
        /**参数校验*/
        if(bindingResult.hasErrors()){
            throw new ValidException(bindingResult.getFieldError().getDefaultMessage());
        }
        return userService.updateInfo(user);
    }

    @GetMapping("/info/get")
    public Result getInfo(@RequestParam("id") Long id){
        return userService.getInfo(id);
    }


    @GetMapping("/location/get")
    public Result refreshLocation(@RequestParam("driverCarId") Long driverCarId,@RequestParam("driverCarBatchId") Long driverCarBatchId){
        return userService.refreshLocation(driverCarId,driverCarBatchId);
    }

    /***
     *  查询附近的司机
     * */
    @GetMapping("/location/near/drivers")
    public Result getNearDriverLocations(@RequestParam("lng") BigDecimal lng,
                                         @RequestParam("lat") BigDecimal lat){
        return driverService.getNearDriverLocations(lng, lat);
    }



}
