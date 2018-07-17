package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.vo.DriverCarAddVo;
import com.ga.wyc.domain.vo.DriverLoginVo;

public interface IDriverService {

    //添加一个新的司机和对应的车
    Result addAndCar(DriverCarAddVo vo);

    Result login(DriverLoginVo loginVo);

    /**
     *  自动登陆接口 不判断token的时效性 比对正确性即可
     * */
    Result autoLogin(DriverLoginVo loginVo);

    Result refreshToken(String phone,String token,String refreshToken);

    Result sendSmsLogin(String phone);

    Result getInfo(Long id);

    /***
     *  获取司机的车辆列表
     * */
    Result getCars(Long id);
}