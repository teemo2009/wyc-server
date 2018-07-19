package com.ga.wyc.domain.vo;

import com.ga.wyc.domain.bean.Location;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.group.IRefreshLocation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 *  刷新坐标
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors
public class DriverCarRefreshVo extends DriverCar {
    //坐标的二维数组
    @NotNull(groups = {IRefreshLocation.class},message = "坐标数组不能为空")
    private List<Location> locations;
}
