package com.ga.wyc;

import com.ga.wyc.domain.entity.Car;
import com.ga.wyc.domain.entity.Driver;
import com.ga.wyc.domain.entity.DriverCar;
import com.ga.wyc.domain.vo.DriverCarAddVo;
import com.ga.wyc.service.IDriverService;
import com.ga.wyc.util.MUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WycDriverBootApplicationTests {

	@Resource
	IDriverService driverService;

	@Resource
	MUtil mUtil;

	@Test
	public void contextLoads() {
	}


	@Test
	public void initDriverCar1(){
		DriverCarAddVo vo=new DriverCarAddVo();
		Driver driver=new Driver();
		driver.setCode(mUtil.UUID());
		driver.setGender("1");
		driver.setName("李师傅");
		driver.setPhone("18685147299");
		vo.setDriver(driver);
		Car car=new Car();
		car.setCode(mUtil.UUID());
		car.setVehicleNo("贵A8999");
		car.setSeats(4);
		car.setModel("大众");
		car.setVehicleType("商务轿车");
		car.setVehicleColor("白色");
		vo.setCar(car);
		driverService.addAndCar(vo);
	}


	@Test
	public void initDriverCar2(){
		DriverCarAddVo vo=new DriverCarAddVo();
		Driver driver=new Driver();
		driver.setCode(mUtil.UUID());
		driver.setGender("1");
		driver.setName("王师傅");
		driver.setPhone("18685147297");
		vo.setDriver(driver);
		Car car=new Car();
		car.setCode(mUtil.UUID());
		car.setVehicleNo("贵B8999");
		car.setSeats(4);
		car.setModel("雪佛兰");
		car.setVehicleType("商务轿车");
		car.setVehicleColor("红色");
		vo.setCar(car);
		driverService.addAndCar(vo);
	}

}
