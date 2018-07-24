package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.Manager;

public interface ManagerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Manager record);

    int insertSelective(Manager record);

    Manager selectByPrimaryKey(Long id);

    Manager selectOneSelective(Manager manager);

    int updateByPrimaryKeySelective(Manager record);

    int updateByPrimaryKey(Manager record);
}