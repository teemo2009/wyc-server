package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.DriverCar;

import java.util.List;
import java.util.Map;

public interface DriverCarMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverCar record);

    int insertSelective(DriverCar record);

    DriverCar selectByPrimaryKey(Long id);

    DriverCar selectOneSelective(DriverCar record);

    List<DriverCar> selectNearDriverList(Map<String,Object> paramMap);

    int updateByPrimaryKeySelective(DriverCar record);

    int updateByPrimaryKey(DriverCar record);
}