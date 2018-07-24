package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/token/check")
    public Result tokenCheck(){
        return Result.success().message("验证成功");
    }



}
