package com.ga.wyc.domain.entity;

import com.ga.wyc.domain.bean.BaseEntity;
import com.ga.wyc.domain.enums.CarPublish;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DriverCar extends BaseEntity {
    private Long id;

    private Long driverId;

    private Long carId;

    private CarPublish publish;

}