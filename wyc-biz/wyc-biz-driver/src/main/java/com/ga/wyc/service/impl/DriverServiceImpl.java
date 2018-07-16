package com.ga.wyc.service.impl;

import com.ga.wyc.dao.CarMapper;
import com.ga.wyc.dao.DriverCarMapper;
import com.ga.wyc.dao.DriverMapper;
import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.Car;
import com.ga.wyc.domain.entity.Driver;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.vo.DriverCarAddVo;
import com.ga.wyc.service.IDriverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("driverService")
public class DriverServiceImpl implements IDriverService {
    @Resource
    DriverMapper driverMapper;
    @Resource
    CarMapper carMapper;
    @Resource
    DriverCarMapper driverCarMapper;

    @Override
    @Transactional
    public Result addAndCar(DriverCarAddVo vo) {
        Driver driver= vo.getDriver();
        //首先添加司机的基础信息
        driverMapper.insertSelective(driver);
        Long driverId=driver.getId();
        Car car =vo.getCar();
        //其次初始化车的信息 注意车牌号
        carMapper.insertSelective(car);
        //关联起来
        DriverCar driverCar=new DriverCar();
        driverCar.setCarId(car.getId());
        driverCar.setDriverId(driverId);
        driverCarMapper.insertSelective(driverCar);
        return Result.success().message("添加成功");
    }

}
