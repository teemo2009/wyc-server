package com.ga.wyc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ga.wyc.dao")
public class WycAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(WycAdminApplication.class, args);
    }
}
