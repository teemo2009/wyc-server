package com.ga.wyc.domain.entity;

import com.ga.wyc.domain.bean.BaseEntity;
import com.ga.wyc.domain.enums.CarPublish;
import com.ga.wyc.domain.enums.DriverCarState;
import com.ga.wyc.domain.group.IRefreshLocation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DriverCar extends BaseEntity {
    @NotNull(groups = {IRefreshLocation.class},message = "id不能为空")
    private Long id;
    private Long driverId;
    private Long carId;
    @NotNull(groups = {IRefreshLocation.class},message = "车辆批次ID不能为空")
    private Long driverCarBatchId;
    private CarPublish publish;
    private DriverCarState state;
    @NotNull(groups = {IRefreshLocation.class},message = "经度不能为空")
    private Double lastLng;
    @NotNull(groups = {IRefreshLocation.class},message = "纬度不能为空")
    private Double lastLat;
}