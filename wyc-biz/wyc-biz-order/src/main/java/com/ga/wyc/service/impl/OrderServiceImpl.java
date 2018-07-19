package com.ga.wyc.service.impl;

import com.ga.wyc.RedisUtil;
import com.ga.wyc.dao.DriverCarMapper;
import com.ga.wyc.dao.OrderMapper;
import com.ga.wyc.domain.bean.BusinessException;
import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.dto.OrderDTO;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.domain.enums.CarPublish;
import com.ga.wyc.domain.enums.DriverCarState;
import com.ga.wyc.domain.enums.OrderState;
import com.ga.wyc.service.IOrderSerive;
import com.ga.wyc.util.MUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("orderService")
@Slf4j
public class OrderServiceImpl implements IOrderSerive {

    @Resource
    OrderMapper orderMapper;
    @Resource
    DriverCarMapper driverCarMapper;
    @Resource
    MUtil mUtil;
    @Resource
    RedisUtil redisUtil;

    @Value("${redis.orderList}")
    String REDIS_ORDER_LIST;

    @Value("${redis.driverOrder}")
    String REDIS_DRIVER_ORDER;



    @Override
    public synchronized Result initOrder(Order order) {
        //用户在订单没有支付完成或取消前  不可以 继续发起订单
       Order userHasOrder= orderMapper.userHasOrdering(order.getUserId());
       if(!ObjectUtils.isEmpty(userHasOrder)){
           throw new BusinessException("当前有订单未处理完成，不能下单");
       }
       //生成order编码
       order.setCode(mUtil.UUID());
       orderMapper.insertSelective(order);
       //判断redis中是否订单的队列没有初始化
        String key=REDIS_ORDER_LIST;
        List<Order> orders;
        if(!redisUtil.hasKey(key)){
            orders=new ArrayList<>();
        }else{
            orders=redisUtil.get(key);
        }
        orders.add(order);
        redisUtil.put(key,orders);
        return Result.success().message("发起成功").data(order.getId());
    }

    @Override
    @Transactional
    public synchronized Result tackOrder(Order order) {
        //TODO 这里会涉及到多线程抢单问题，现在暂时使用同步，但在分布式环境下，这种方案并不是最优！
        //判断司机当前是否在发车且为闲置状态
        DriverCar driverHasRunning=driverCarMapper.selectByPrimaryKey(order.getDriverCarId());
        if (!driverHasRunning.getPublish().equals(CarPublish.START)){
            throw new BusinessException("当前司机未出车，不能接单");
        }
        if(driverHasRunning.getState().equals(DriverCarState.RUNNING)){
            throw new BusinessException("当前有订单未处理完成，不能接单");
        }
        Long driverCarBatchId=driverHasRunning.getDriverCarBatchId();
        //判断订单是否可以抢
        Order dbOrder= orderMapper.selectByPrimaryKey(order.getId());
        if(!dbOrder.getState().equals(OrderState.INIT)){
            throw new BusinessException("当前有订单已被接单");
        }
        //更新订单
        order.setState(OrderState.TAKING);
        order.setDriverCarBatchId(driverCarBatchId);
        orderMapper.updateByPrimaryKeySelective(order);
        //更新司机的状态为接客
        DriverCar driverCarRecord=new DriverCar().setId(order.getDriverCarId()).setState(DriverCarState.RUNNING);
        driverCarMapper.updateByPrimaryKeySelective(driverCarRecord);
        return Result.success().message("接单成功");
    }

    @Override
    public Result getPublishOrder(Long driverCarId) {
        String keyDriver=REDIS_DRIVER_ORDER+driverCarId;
        Order order= redisUtil.get(keyDriver);
        return Result.success().data(order);
    }

    @Override
    public Result cancelPublishOrder(Long driverCarId) {
        String keyDriver=REDIS_DRIVER_ORDER+driverCarId;
        redisUtil.remove(keyDriver);
        return Result.success().message("取消成功");
    }

    @Override
    public synchronized Result startOrder(Order order) {
        //司机接到客户
        //判断是否为司机 发起的订单
        //
        return null;
    }

    @Override
    public Result reachOrder(Order order) {
        return null;
    }

    @Override
    public Result finishOrder(Order order) {
        return null;
    }

    @Override
    public Result initCancel(Order order) {
        return null;
    }

    @Override
    public Result driverCancel(Order order) {
        return null;
    }

    @Override
    public Result customCancel(Order order) {
        return null;
    }

    @Override
    public Result getOrderList(Double longitude, Double latitude) {
        Map<String,Object> paramMap=getRangeMap(longitude,latitude);
        paramMap.put("state",OrderState.INIT);
        List<OrderDTO> list= orderMapper.selectNearOrderList(paramMap);
        return Result.success().data(list);
    }

    @Override
    public List<DriverCar> getDriverByOrderLngLat(Double longitude, Double latitude) {
        Map<String,Object> paramMap=getRangeMap(longitude,latitude);
        paramMap.put("publish", CarPublish.START);
        paramMap.put("state",DriverCarState.IDLE);
        List<DriverCar> list= orderMapper.selectNearDriverList(paramMap);
        return list;
    }


    private Map<String,Object> getRangeMap(Double longitude,Double latitude){
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 10;//10千米距离
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        BigDecimal minlat =new BigDecimal(latitude-dlat).setScale(6,BigDecimal.ROUND_HALF_UP);
        BigDecimal maxlat = new BigDecimal(latitude+dlat).setScale(6,BigDecimal.ROUND_HALF_UP);
        BigDecimal minlng = new BigDecimal(longitude -dlng).setScale(6,BigDecimal.ROUND_HALF_UP);
        BigDecimal maxlng = new BigDecimal(longitude + dlng).setScale(6,BigDecimal.ROUND_HALF_UP);
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("minlng",minlng);
        paramMap.put("maxlng",maxlng);
        paramMap.put("minlat",minlat);
        paramMap.put("maxlat",maxlat);
        return paramMap;
    }
}
