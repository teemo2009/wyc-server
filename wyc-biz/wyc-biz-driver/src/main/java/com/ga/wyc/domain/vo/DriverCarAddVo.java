package com.ga.wyc.domain.vo;


import com.ga.wyc.domain.entity.Car;
import com.ga.wyc.domain.entity.Driver;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DriverCarAddVo{
    Driver driver;
    Car car;
}
