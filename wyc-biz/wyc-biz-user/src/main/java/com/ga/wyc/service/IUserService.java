package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.User;

public interface IUserService {
      Result login(String phone,String code);

      Result refreshToken(String phone,String token,String refreshToken);

      Result sendSmsLogin(String phone);
}
