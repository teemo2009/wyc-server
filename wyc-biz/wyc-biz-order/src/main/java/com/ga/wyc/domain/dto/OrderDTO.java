package com.ga.wyc.domain.dto;

import com.ga.wyc.domain.entity.Car;
import com.ga.wyc.domain.entity.Driver;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.domain.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class OrderDTO extends Order {
    private User user;
    private Driver driver;
    private Car car;
}
