package com.ga.wyc.domain.dto;

import com.ga.wyc.domain.entity.Car;
import com.ga.wyc.domain.entity.Driver;
import com.ga.wyc.domain.entity.DriverCar;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class DriverCarDTO extends DriverCar {
    private Driver driver;
    private Car car;
}
