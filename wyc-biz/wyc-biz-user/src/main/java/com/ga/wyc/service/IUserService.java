package com.ga.wyc.service;

import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.entity.User;

public interface IUserService {
      Result login(String phone,String code);

      /**
       *  自动登陆接口 不判断token的时效性 比对正确性即可
       * */
      Result autoLogin(String phone,String token);

      Result refreshToken(String phone,String token,String refreshToken);

      Result sendSmsLogin(String phone);

      Result updateInfo(User user);

      Result getInfo(Long id);
}
