package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.Manager;

public interface IManagerService {

        Result add(Manager manager);

        Result login(Manager manager);

        Result refreshToken(String userName,String token,String refreshToken);

}
