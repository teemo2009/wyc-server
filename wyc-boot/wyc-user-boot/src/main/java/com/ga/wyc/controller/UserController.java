package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.entity.User;
import com.ga.wyc.service.IUserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;

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





}
