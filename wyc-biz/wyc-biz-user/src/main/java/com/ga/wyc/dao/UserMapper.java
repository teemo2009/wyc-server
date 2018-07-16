package com.ga.wyc.dao;

import com.ga.wyc.domain.entity.User;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    User selectOneSelective(User user);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}