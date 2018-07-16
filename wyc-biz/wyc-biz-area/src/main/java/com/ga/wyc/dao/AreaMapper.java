package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.Area;

import java.util.List;

public interface AreaMapper {
    int deleteByPrimaryKey(String adcode);

    int insert(Area record);

    int insertSelective(Area record);

    int batchInsert(List<Area> list);

    Area selectByPrimaryKey(String adcode);

    int updateByPrimaryKeySelective(Area record);

    int updateByPrimaryKey(Area record);
}