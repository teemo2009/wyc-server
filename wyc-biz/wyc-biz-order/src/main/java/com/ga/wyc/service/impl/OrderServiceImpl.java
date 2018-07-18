package com.ga.wyc.service.impl;

import com.ga.wyc.dao.DriverCarMapper;
import com.ga.wyc.dao.OrderMapper;
import com.ga.wyc.domain.bean.BusinessException;
import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.dto.OrderDTO;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.domain.enums.DriverCarState;
import com.ga.wyc.domain.enums.OrderState;
import com.ga.wyc.service.IOrderSerive;
import com.ga.wyc.util.MUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
       return Result.success().message("发起成功").data(order.getId());
    }

    @Override
    @Transactional
    public synchronized Result tackOrder(Order order) {
        //TODO 这里会涉及到多线程抢单问题，现在暂时使用同步，但在分布式环境下，这种方案并不是最优！
        //判断是否在 为接单，或派送，或未完成状态
        Order driverHasOrder=orderMapper.driverHasOrdering(order.getDriverCarId());
        if(!ObjectUtils.isEmpty(driverHasOrder)){
            throw new BusinessException("当前有订单未处理完成，不能接单");
        }
        DriverCar driverHasRunning=driverCarMapper.selectByPrimaryKey(order.getDriverCarId());
        if(driverHasRunning.getState().equals(DriverCarState.RUNNING)){
            throw new BusinessException("当前有订单未处理完成，不能接单");
        }
        //判断订单是否可以抢
        Order dbOrder= orderMapper.selectByPrimaryKey(order.getId());
        if(!dbOrder.getState().equals(OrderState.INIT)){
            throw new BusinessException("当前有订单已被接单");
        }
        //更新订单
        order.setState(OrderState.TAKING);
        orderMapper.updateByPrimaryKeySelective(order);
        //更新司机的状态为接客
        DriverCar driverCarRecord=new DriverCar().setId(order.getDriverCarId()).setState(DriverCarState.RUNNING);
        driverCarMapper.updateByPrimaryKeySelective(driverCarRecord);
        return Result.success().message("接单成功");
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
        //根据当前经纬度 计算 10公里以内的订单 //26.601497,106.622920
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 10;//10千米距离
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        BigDecimal minlat =new BigDecimal(latitude-dlat);
        BigDecimal maxlat = new BigDecimal(latitude+dlat);
        BigDecimal minlng = new BigDecimal(longitude -dlng);
        BigDecimal maxlng = new BigDecimal(longitude + dlng);
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("minlng",minlng);
        paramMap.put("maxlng",maxlng);
        paramMap.put("minlat",minlat);
        paramMap.put("maxlat",maxlat);
        //此处枚举比较特殊
        paramMap.put("state",OrderState.INIT);
        List<OrderDTO> list= orderMapper.selectNearOrderList(paramMap);
        return Result.success().data(list);
    }



}
