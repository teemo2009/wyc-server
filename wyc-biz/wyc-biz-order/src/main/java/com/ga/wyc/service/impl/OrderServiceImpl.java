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
import com.ga.wyc.domain.enums.PayState;
import com.ga.wyc.service.IOrderSerive;
import com.ga.wyc.util.MUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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

    @Value("${redis.orderPush}")
    String REDIS_ORDER_PUSH;


    @Override
    public synchronized Result initOrder(Order order) {
        //用户在订单没有支付完成或取消前  不可以 继续发起订单
        Order userHasOrder = orderMapper.userHasOrdering(order.getUserId());
        if (!ObjectUtils.isEmpty(userHasOrder)) {
            throw new BusinessException("当前有订单未处理完成，不能下单");
        }
        //生成order编码
        order.setCode(mUtil.UUID());
        orderMapper.insertSelective(order);
        //判断redis中是否订单的队列没有初始化
        String key = REDIS_ORDER_LIST;
        List<Order> orders;
        if (!redisUtil.hasKey(key)) {
            orders = new ArrayList<>();
        } else {
            orders = redisUtil.get(key);
        }
        orders.add(order);
        redisUtil.put(key, orders);
        return Result.success().message("发起成功").data(order.getId());
    }

    @Override
    @Transactional
    public synchronized Result tackOrder(Order order) {
        //TODO 这里会涉及到多线程抢单问题，现在暂时使用同步，但在分布式环境下，这种方案并不是最优！
        //判断司机当前是否在发车且为闲置状态
        DriverCar driverHasRunning = driverCarMapper.selectByPrimaryKey(order.getDriverCarId());
        if (!driverHasRunning.getPublish().equals(CarPublish.START)) {
            throw new BusinessException("当前司机未出车，不能接单");
        }
        if (driverHasRunning.getState().equals(DriverCarState.RUNNING)) {
            throw new BusinessException("当前有订单未处理完成，不能接单");
        }
        Long driverCarBatchId = driverHasRunning.getDriverCarBatchId();
        //判断订单是否可以抢
        Order dbOrder = orderMapper.selectByPrimaryKey(order.getId());
        if (!dbOrder.getState().equals(OrderState.INIT)) {
            throw new BusinessException("当前有订单已被接单");
        }
        //更新订单
        order.setState(OrderState.TAKING);
        order.setDriverCarBatchId(driverCarBatchId);
        orderMapper.updateByPrimaryKeySelective(order);
        //更新司机的状态为接客
        DriverCar driverCarRecord = new DriverCar().setId(order.getDriverCarId()).setState(DriverCarState.RUNNING);
        driverCarMapper.updateByPrimaryKeySelective(driverCarRecord);
        return Result.success().message("接单成功");
    }


    @Override
    public Result driverCancelOrderInit(Long driverCarId) {
        //取消推送的订单
        String keyDriver = REDIS_DRIVER_ORDER + driverCarId;
        String keyOrderList = REDIS_ORDER_LIST;
        if (redisUtil.hasKey(keyOrderList) && redisUtil.hasKey(keyDriver)) {
            List<Order> orders = redisUtil.get(keyOrderList);
            Order cacheOrder = redisUtil.get(keyDriver);
            Order detailOrder = orderMapper.selectByPrimaryKey(cacheOrder.getId());
            //返回订单队列
            orders.add(detailOrder);
            redisUtil.put(keyOrderList, orders);
            //删除司机的订单相关缓存
            redisUtil.remove(keyDriver);
        } else {
            return Result.success().message("没有订单可以取消");
        }
        return Result.success().message("取消成功");
    }

    @Override
    public synchronized Result startOrder(Order order) {
        //司机接到客户
        //判断订单是否存在
        Order db = orderMapper.selectByPrimaryKey(order.getId());
        if (ObjectUtils.isEmpty(db)) {
            throw new BusinessException("订单不存在");
        }
        //判断是否为当前司机所接的订单
        if (order.getDriverCarId() != db.getDriverCarId()) {
            throw new BusinessException("您不是当前接单司机，无权操作");
        }
        //判断订单状态是否为司机接单状态
        if (!db.getState().equals(OrderState.TAKING)) {
            throw new BusinessException("当前订单状态异常，无法操作");
        }
        //开始更新
        // wait_mile（空驶里程 来自客户端）
        // wait_time（空驶时间来自客户端）
        // dep_time（乘客上车时间来自服务端）
        // state 状态改变（来自服务端）
        order.setDepTime(new Date());
        order.setState(OrderState.STARTING);
        orderMapper.updateByPrimaryKeySelective(order);
        return Result.success().message("接客成功");

    }

    @Override
    public Result reachOrder(Order order) {
        //乘客下车
        //判断订单是否存在
        Order db = orderMapper.selectByPrimaryKey(order.getId());
        if (ObjectUtils.isEmpty(db)) {
            throw new BusinessException("订单不存在");
        }
        //判断是否为当前司机所接的订单
        if (order.getDriverCarId() != db.getDriverCarId()) {
            throw new BusinessException("您不是当前接单司机，无权操作");
        }
        //判断订单状态是否为司机接单状态
        if (!db.getState().equals(OrderState.STARTING)) {
            throw new BusinessException("当前订单状态异常，无法操作");
        }
        Date depTime=db.getDepTime();
        //乘客下车时间
        Date nowTime=new Date();
        //载客时间 s
        int driveTime= (int)((nowTime.getTime()-depTime.getTime())/1000);
        //计算金额
        Map<String,BigDecimal> priceMap= (Map<String, BigDecimal>) computedPrice(order.getDriveMile(),driveTime).data();
        //实际金额
        BigDecimal factPrice=priceMap.get("totalPrice");
        //远途加价金额
        BigDecimal farUpPrice=new BigDecimal(0);
        if(priceMap.containsKey("farUpPrice")){
            farUpPrice.add(priceMap.get("farUpPrice"));
        }
        //更新
        //1.dest_time 乘客下车时间（服务端）
        //2.drive_mile 载客里程（客户端）
        //3.drive_time 载客时间（服务端计算）
        //4.fact_price 实收金额（服务端计算）
        //5.far_up_price 远途加价金额（服务端计算）
        //6.other_up_price 其它加价金额（暂时不考虑）
        Order updateRecord=new Order().setId(order.getId()).setDestTime(nowTime)
                .setDriveTime(driveTime).setFactPrice(factPrice).setFarUpPrice(farUpPrice).setState(OrderState.REACH);
        orderMapper.updateByPrimaryKeySelective(updateRecord);
        return Result.success().message("操作成功").data(updateRecord);
    }

    @Override
    public Result finishOrder(Order order) {
        //乘客下车
        //判断订单是否存在
        Order db = orderMapper.selectByPrimaryKey(order.getId());
        if (ObjectUtils.isEmpty(db)) {
            throw new BusinessException("订单不存在");
        }
        //判断是否为当前用户的订单
        if (order.getUserId() != db.getUserId()) {
            throw new BusinessException("您不是当前用户，无权操作");
        }
        //判断订单状态是否为可支付状态
        if (!db.getState().equals(OrderState.REACH)) {
            throw new BusinessException("当前订单状态异常，无法操作");
        }
        // 需要更新 finish_time state pay_state
        Order updateRecord=new Order().setId(order.getId()).setFinishTime(new Date()).setState(OrderState.FINISH).setPayState(PayState.PAYED);
        orderMapper.updateByPrimaryKeySelective(updateRecord);
        //修改司机的状态，回到初始化
        initDriverCar(db.getDriverCarId());
        return Result.success().message("操作成功");
    }

    @Override
    public Result computedPrice(BigDecimal mile, Integer time) {
        Map<String, BigDecimal> rsMap = new HashMap<>();
        BigDecimal totalPrice = new BigDecimal(10)
                .add(mile.multiply(new BigDecimal(1.6D)))
                .add(new BigDecimal(time / 60).multiply(new BigDecimal(0.5)));
        if (mile.compareTo(new BigDecimal(20)) == 1) {
            BigDecimal farUpPrice = mile.subtract(new BigDecimal(20)).multiply(new BigDecimal(0.8D));
            farUpPrice=farUpPrice.setScale(2,BigDecimal.ROUND_HALF_UP);
            //需要远途加价
            totalPrice.add(farUpPrice);
            rsMap.put("farUpPrice", farUpPrice);
        }
        totalPrice=totalPrice.setScale(2,BigDecimal.ROUND_HALF_UP);
        rsMap.put("totalPrice", totalPrice);
        return Result.success().data(rsMap);
    }


    @Override
    @Transactional
    public Result customeCancel(Order order) {
        //乘客取消
        //判断订单是否存在
        Order db = orderMapper.selectByPrimaryKey(order.getId());
        if (ObjectUtils.isEmpty(db)) {
            throw new BusinessException("订单不存在");
        }
        //判断是否为发单乘客
        if(!db.getUserId().equals(order.getUserId())){
            throw new BusinessException("你不是订单发起乘客，无权操作");
        }
        //判断是否为可以取消
        if(!db.getState().equals(OrderState.INIT) &&
                !db.getState().equals(OrderState.TAKING)){
            throw new BusinessException("当前订单状态异常，无法操作");
        }
        Order updateRecord;
        if(ObjectUtils.isEmpty(db.getDriverCarId())){

            //没有司机接单
            //从队列中删除订单
            String key = REDIS_ORDER_LIST;
            if (!redisUtil.hasKey(key)) {
               List<Order> orders = new ArrayList<>();
               int currIndex=0;
               for (int i=0;i<orders.size();i++){
                   if(orders.get(i).getId()==db.getId()){
                       currIndex=i;
                   }
               }
               if(currIndex>0){
                   orders.remove(currIndex);
                   redisUtil.put(key,orders);
               }
            }

            updateRecord=new Order().setId(db.getId()).setState(OrderState.INIT_CANCEL);
            orderMapper.updateByPrimaryKeySelective(updateRecord);
            //redis删除出对应的司机
            String keyPush=REDIS_ORDER_PUSH+db.getId();
            if (redisUtil.hasKey(keyPush)){
                Long cacheDriverCarId=Long.parseLong(redisUtil.get(keyPush)+"");
                String keyDriver=REDIS_DRIVER_ORDER+cacheDriverCarId;
                redisUtil.remove(keyDriver);
            }
        }else{
            //司机接单
            updateRecord=new Order().setId(db.getId()).setState(OrderState.CUSTOME_CANCEL);
            orderMapper.updateByPrimaryKeySelective(updateRecord);
            //修改司机的状态，回到初始化
            initDriverCar(db.getDriverCarId());
        }
        return Result.success().message("操作成功");
    }

    @Override
    public Result driverCancel(Order order) {
        //司机取消
        //判断订单是否存在
        Order db = orderMapper.selectByPrimaryKey(order.getId());
        if (ObjectUtils.isEmpty(db)) {
            throw new BusinessException("订单不存在");
        }
        //判断是否为当前司机
        if(!db.getDriverCarId().equals(order.getDriverCarId())){
            throw new BusinessException("你不是当前司机，无权操作");
        }
        //判断是否为可以取消
        if(!db.getState().equals(OrderState.TAKING)){
            throw new BusinessException("当前订单状态异常，无法操作");
        }
        Order updateRecord=new Order().setId(db.getId()).setState(OrderState.DRIVER_CANCEL);
        orderMapper.updateByPrimaryKeySelective(updateRecord);
        //修改司机的状态，回到初始化
        initDriverCar(db.getDriverCarId());
        return Result.success().message("操作成功");
    }


    @Override
    public Result getOrderList(Double longitude, Double latitude) {
        Map<String, Object> paramMap = getRangeMap(longitude, latitude);
        paramMap.put("state", OrderState.INIT);
        List<OrderDTO> list = orderMapper.selectNearOrderList(paramMap);
        return Result.success().data(list);
    }

    @Override
    public Result refreshDriverOrder(Long driverCarId, Long driverCarBatchId) {
        String keyDriver = REDIS_DRIVER_ORDER + driverCarId;
        if (redisUtil.hasKey(keyDriver)) {
            Order currOrder = redisUtil.get(keyDriver);
            OrderDTO orderDTO = orderMapper
                    .selectOrderDTOOne(new Order().setId(currOrder.getId()));
            return Result.success().data(orderDTO);
        }
        return Result.success().message("当前没有订单");
    }

    @Override
    public Result refreshUserOrder(Long userId) {
        OrderDTO orderDTO = orderMapper.selectUserOrderingOne(new Order().setUserId(userId));
        if(ObjectUtils.isEmpty(orderDTO)){
            throw new BusinessException("当前没有订单");
        }
        return Result.success().data(orderDTO);
    }

    @Override
    public List<DriverCar> getDriverByOrderLngLat(Double longitude, Double latitude) {
        Map<String, Object> paramMap = getRangeMap(longitude, latitude);
        paramMap.put("publish", CarPublish.START);
        paramMap.put("state", DriverCarState.IDLE);
        List<DriverCar> list = orderMapper.selectNearDriverList(paramMap);
        return list;
    }

    //初始化司机状态
    private void initDriverCar(Long driverCarId){
        DriverCar driverCarUpdateRecord=new DriverCar();
        //修改为闲置
        driverCarUpdateRecord.setId(driverCarId).setState(DriverCarState.IDLE);
        driverCarMapper.updateByPrimaryKeySelective(driverCarUpdateRecord);
        //删除redis锁，使得司机可以接单
        String keyDriver = REDIS_DRIVER_ORDER + driverCarId;
        if(redisUtil.hasKey(keyDriver)){
            redisUtil.remove(keyDriver);
        }
    }

    private Map<String, Object> getRangeMap(Double longitude, Double latitude) {
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 10;//10千米距离
        double dlng = 2 * Math.asin(Math.sin(dis / (2 * r)) / Math.cos(latitude * Math.PI / 180));
        dlng = dlng * 180 / Math.PI;//角度转为弧度
        double dlat = dis / r;
        dlat = dlat * 180 / Math.PI;
        BigDecimal minlat = new BigDecimal(latitude - dlat).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal maxlat = new BigDecimal(latitude + dlat).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal minlng = new BigDecimal(longitude - dlng).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal maxlng = new BigDecimal(longitude + dlng).setScale(6, BigDecimal.ROUND_HALF_UP);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("minlng", minlng);
        paramMap.put("maxlng", maxlng);
        paramMap.put("minlat", minlat);
        paramMap.put("maxlat", maxlat);
        return paramMap;
    }


}
