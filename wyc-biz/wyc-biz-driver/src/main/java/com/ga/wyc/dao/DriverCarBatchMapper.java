package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.DriverCarBatch;

public interface DriverCarBatchMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverCarBatch record);

    int insertSelective(DriverCarBatch record);

    DriverCarBatch selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DriverCarBatch record);

    int updateByPrimaryKey(DriverCarBatch record);
}