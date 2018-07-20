package com.ga.wyc.dao;

import com.ga.wyc.domain.dto.OrderDTO;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long id);

    OrderDTO selectOrderDTOOne(Order record);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order userHasOrdering(Long userId);

    Order driverHasOrdering(Long driverCarId);

    List<OrderDTO> selectNearOrderList(Map<String,Object> param);

    List<DriverCar> selectNearDriverList(Map<String,Object> param);
}