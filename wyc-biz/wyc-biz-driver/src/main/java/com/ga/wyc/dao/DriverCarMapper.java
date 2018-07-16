package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.DriverCar;

public interface DriverCarMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverCar record);

    int insertSelective(DriverCar record);

    DriverCar selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DriverCar record);

    int updateByPrimaryKey(DriverCar record);
}