package com.ga.wyc;

import com.ga.wyc.domain.entity.Manager;
import com.ga.wyc.domain.enums.ManagerState;
import com.ga.wyc.service.IManagerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WycAdminApplicationTests {

    @Resource
    IManagerService managerService;

    @Test
    public void contextLoads() {
    }


    @Test
    public void addManagerTest(){
        Manager manager=new Manager();
        manager.setUserName("wycadmin");
        manager.setPassword("wycadmin123");
        manager.setRealName("卢启");
        manager.setPhone("18685147291");
        manager.setState(ManagerState.ABLE);
        managerService.add(manager);
    }


}
