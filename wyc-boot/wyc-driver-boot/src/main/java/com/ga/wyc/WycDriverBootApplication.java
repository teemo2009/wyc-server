package com.ga.wyc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.ga.wyc.dao")
public class WycDriverBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(WycDriverBootApplication.class, args);
	}
}
