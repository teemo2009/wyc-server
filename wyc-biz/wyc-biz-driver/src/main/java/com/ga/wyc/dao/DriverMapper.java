package com.ga.wyc.dao;

import com.ga.wyc.domain.dto.DriverCarDTO;
import com.ga.wyc.domain.dto.DriverDTO;
import com.ga.wyc.domain.entity.Car;
import com.ga.wyc.domain.entity.Driver;
import com.ga.wyc.domain.entity.DriverCar;

import java.util.List;

public interface DriverMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Driver record);

    int insertSelective(Driver record);

    Driver selectOneSelective(Driver driver);

    Driver selectByPrimaryKey(Long id);

    List<DriverCarDTO> selectDriverCarsByID(Long driverId);

    DriverCarDTO selectDriverPublishCar(DriverCar driverCar);

    int updateByPrimaryKeySelective(Driver record);

    int updateByPrimaryKey(Driver record);
}