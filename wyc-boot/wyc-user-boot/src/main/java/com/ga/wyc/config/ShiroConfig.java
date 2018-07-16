package com.ga.wyc.config;

import com.ga.wyc.domain.bean.CustomRealm;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ShiroConfig extends BaseShiroConfig  {

    @Override
    public BaseCustomRealm customRealm() {
        CustomRealm customRealm=new CustomRealm();
        return customRealm;
    }

    @Override
    public List<String> filterByAnon() {
        List<String> list=new ArrayList<>();
        list.add("/account/**");
        //area 初始化接口
        list.add("/area/init");
        return list;
    }
}
