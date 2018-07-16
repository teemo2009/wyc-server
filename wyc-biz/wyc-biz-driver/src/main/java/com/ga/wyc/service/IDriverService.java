package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.vo.DriverCarAddVo;

public interface IDriverService {

    //添加一个新的司机和对应的车
    Result addAndCar(DriverCarAddVo vo);


}
