package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.DriverDev;

public interface DriverDevMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverDev record);

    int insertSelective(DriverDev record);

    DriverDev selectByPrimaryKey(Long id);

    DriverDev selectOneSelective(DriverDev record);

    int updateByPrimaryKeySelective(DriverDev record);

    int updateByPrimaryKey(DriverDev record);
}