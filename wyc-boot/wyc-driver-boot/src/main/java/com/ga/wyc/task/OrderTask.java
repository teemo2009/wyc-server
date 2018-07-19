package com.ga.wyc.task;

import com.ga.wyc.RedisUtil;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.entity.Order;
import com.ga.wyc.service.IOrderSerive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Configurable
@Slf4j
@EnableAsync
public class OrderTask {



    @Resource
    RedisUtil redisUtil;


    @Value("${redis.orderList}")
    String REDIS_ORDER_LIST;

    @Value("${redis.driverOrder}")
    String REDIS_DRIVER_ORDER;

    @Resource
    IOrderSerive orderSerive;

    /**
     * 每隔一秒执行一次，定时器在本次任务执行完成后才会自动执行 下一次
     */
    @Scheduled(fixedDelay = 1000)
    @Async
    public void tracOrder() throws InterruptedException {
            if(redisUtil.hasKey(REDIS_ORDER_LIST)){
                //判断是否订单存在
                List<Order> orderList= redisUtil.get(REDIS_ORDER_LIST);
                if(orderList.size()==0){
                    return;
                }
                ExecutorService executor= Executors.newCachedThreadPool();
                CountDownLatch countDownLatch=new CountDownLatch(orderList.size());
                //先来100个线程可以同时执行
                Semaphore orderSp=new Semaphore(100);
                List<Long> finishOrderIds= Collections.synchronizedList(new ArrayList<>());
                for (int i=0;i<orderList.size();i++){
                    int currIndex=i;
                    executor.execute(() -> {
                        try {
                            orderSp.acquire();
                            Order currOrder=orderList.get(currIndex);
                            //获取到订单的起始坐标
                            BigDecimal lng=currOrder.getDepLongitude();
                            BigDecimal lat=currOrder.getDepLatitude();
                            //查询10公里内附近的司机(闲置—发车的)
                            List<DriverCar> driverCarList= orderSerive.getDriverByOrderLngLat(lng.doubleValue(),lat.doubleValue());
                            //目前按照随机数分配
                            int total=driverCarList.size();
                            if(total!=0){
                            Random random=new Random();
                            int index= random.nextInt(total);
                            DriverCar chooseDriverCar=driverCarList.get(index);
                            //给这一名司机分配订单
                            Order tracOrderRecord=new Order();
                            tracOrderRecord.setId(currOrder.getId()).setDriverCarId(chooseDriverCar.getId());
                            String keyDriver=REDIS_DRIVER_ORDER+chooseDriverCar.getId();
                            if (!redisUtil.hasKey(keyDriver)){
                                //判断司机是否在有订单，没有就分配
                                // 订单在取消的时候删除该键值对和结束或正常取消情况下
                                redisUtil.put(keyDriver,tracOrderRecord);
                                //标记这些ID
                                finishOrderIds.add(currOrder.getId());
                                log.info("{}订单已分配给了{}",currOrder.getId(),chooseDriverCar.getId());
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            orderSp.release();
                            countDownLatch.countDown();
                        }
                    });
                }
                //等待所有任务执行完成
                countDownLatch.await();
                //释放相关资源
                executor.shutdown();
                //删除缓存中对应的ID
                if(finishOrderIds.size()!=0){
                    for(int j=0;j<finishOrderIds.size();j++){
                        orderList.remove(j);
                    }
                    redisUtil.put(REDIS_ORDER_LIST,orderList);
                }
                //完成等待下一次 处理
            }
    }


}
