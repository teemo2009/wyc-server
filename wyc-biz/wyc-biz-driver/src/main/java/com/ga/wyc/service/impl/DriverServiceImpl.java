package com.ga.wyc.service.impl;

import com.ga.wyc.RedisUtil;
import com.ga.wyc.dao.*;
import com.ga.wyc.domain.bean.*;
import com.ga.wyc.domain.dto.DriverCarDTO;
import com.ga.wyc.domain.dto.DriverDTO;
import com.ga.wyc.domain.entity.*;
import com.ga.wyc.domain.enums.CarPublish;
import com.ga.wyc.domain.enums.DriverCarState;
import com.ga.wyc.domain.enums.NetType;
import com.ga.wyc.domain.vo.DriverCarAddVo;
import com.ga.wyc.domain.vo.DriverCarRefreshVo;
import com.ga.wyc.domain.vo.DriverLoginVo;
import com.ga.wyc.service.IDriverService;
import com.ga.wyc.util.CodeUtil;
import com.ga.wyc.util.JWTUtil;
import com.ga.wyc.util.MUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("driverService")
public class DriverServiceImpl implements IDriverService {
    @Resource
    MUtil mUtil;
    @Resource
    RedisUtil redisUtil;
    @Resource
    JWTUtil jwtUtil;
    @Resource
    CodeUtil codeUtil;
    @Resource
    DriverMapper driverMapper;
    @Resource
    CarMapper carMapper;
    @Resource
    DriverCarMapper driverCarMapper;
    @Resource
    DriverDevMapper driverDevMapper;
    @Resource
    DriverCarBatchMapper driverCarBatchMapper;

    @Value("${redis.driver}")
    private  String  REDIS_DRIVER;

    @Value("${redis.smsDriver}")
    private String REDIS_SMS_DRIVER;


    @Value("${redis.testCode}")
    private String TEST_CODE;

    @Value("${redis.driverLocation}")
    private String REDIS_DRIVER_LOCATION;

    @Value("${redis.lastLocation}")
    private String REDIS_LAST_LOCATION;

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

    @Override
    @Transactional
    public Result login(DriverLoginVo vo) {
        Driver driver=vo.getDriver();
        DriverDev driverDev=vo.getDriverDev();
        String code=vo.getCode();
        //判断是否发送了验证码
        String smsKey = REDIS_SMS_DRIVER + VerifyCode.Type.LOGIN + ":" + driver.getPhone();
        if (!redisUtil.hasKey(smsKey)) {
            throw new BusinessException("验证码失效");
        }
        String cacheCode = redisUtil.get(smsKey);
        if (!cacheCode.equals(code)) {
            throw new BusinessException("验证码不匹配");
        }
        //判断数据库中是否有注册 对于司机端来说 账号是由后台分配的
        Driver hasPhoneRs = driverMapper.selectOneSelective(new Driver().setPhone(driver.getPhone()));
        if (ObjectUtils.isEmpty(hasPhoneRs)) {
            throw new BusinessException("账号不存在");
        }
        //判断设备代码是否存在
        DriverDev hasDevNo= driverDevMapper.selectOneSelective(new DriverDev().setDevNo(driverDev.getDevNo()));
        if(ObjectUtils.isEmpty(hasDevNo)){
            //不存在 插入数据
            NetType netType=mUtil.matchNum(driver.getPhone());
            driverDev.setNetType(netType);
            driverDev.setDriverId(hasPhoneRs.getId());
            driverDevMapper.insertSelective(driverDev);
        }else{
            //刷新数据 更新时间
            DriverDev updateDev=new DriverDev();
            updateDev.setId(hasDevNo.getId()).setUpdateTime(new Date());
            driverDevMapper.updateByPrimaryKeySelective(updateDev);
        }
        //登陆成功生成token
        String token = jwtUtil.sign(driver.getPhone());
        String refreshToken = codeUtil.encode(token);
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setToken(token);
        driverDTO.setRefreshToken(refreshToken);
        driverDTO.setId(hasPhoneRs.getId());
        driverDTO.setPhone(hasPhoneRs.getPhone());
        String driverKey = REDIS_DRIVER + hasPhoneRs.getPhone();
        redisUtil.put(driverKey, driverDTO);
        //删除短信验证码的缓存
        redisUtil.remove(smsKey);
        return Result.success().message("登陆成功").data(driverDTO);
    }

    @Override
    public Result autoLogin(DriverLoginVo vo) {
        Driver driver=vo.getDriver();
        DriverDev driverDev=vo.getDriverDev();
        String token=vo.getToken();
        String phone=driver.getPhone();
        String driverKey = REDIS_DRIVER + phone;
        //判断token是否异常
        if(!redisUtil.hasKey(driverKey)){
            throw new TokenLoseException();
        }
        DriverDTO driverDTO= redisUtil.get(driverKey);
        if(!driverDTO.getToken().equals(token)){
            //1-在其它手机上重新登陆
            //2-在其它手机调用了refresh_token接口
            throw new TokenLoseException();
        }
        DriverDev hasDevNo=driverDevMapper.selectOneSelective(new DriverDev().setDevNo(driverDev.getDevNo()));
        if(ObjectUtils.isEmpty(hasDevNo)){
            //不存在 插入数据
            NetType netType=mUtil.matchNum(driver.getPhone());
            driverDev.setNetType(netType);
            driverDev.setDriverId(driverDTO.getId());
            driverDevMapper.insertSelective(driverDev);
        }else{
            //刷新数据 更新时间
            DriverDev updateDev=new DriverDev();
            updateDev.setId(hasDevNo.getId()).setUpdateTime(new Date());
            driverDevMapper.updateByPrimaryKeySelective(updateDev);
        }
        //重新生成token
        String newToken = jwtUtil.sign(phone);
        String newRefreshToken = codeUtil.encode(newToken);
        //刷新缓存
        driverDTO.setToken(newToken);
        driverDTO.setRefreshToken(newRefreshToken);
        redisUtil.put(driverKey,driverDTO);
        return Result.success().message("自动登陆成功").data(driverDTO);
    }

    @Override
    public Result refreshToken(String phone, String token, String refreshToken) {
        if (!refreshToken.equals(codeUtil.encode(token))) {
            throw new BusinessException("refresh_token校验失败");
        }
        if (!jwtUtil.verify(phone, token)) {
            throw new BusinessException("token校验失败");
        }
        token = jwtUtil.sign(phone);
        refreshToken = codeUtil.encode(token);
        String driverKey = REDIS_DRIVER + phone;
        //判断token是否异常
        if(!redisUtil.hasKey(driverKey)){
            throw new TokenLoseException();
        }
        DriverDTO driverDTO= redisUtil.get(driverKey);
        driverDTO.setToken(token);
        driverDTO.setRefreshToken(refreshToken);
        //刷新缓存中的token
        redisUtil.put(driverKey,driverDTO);
        Map map = new HashMap(2);
        map.put("token", token);
        map.put("refreshToken", refreshToken);
        return Result.success().data(map);
    }

    @Override
    public Result sendSmsLogin(String phone) {
        //电话号码匹配
        if (!mUtil.isTelphone(phone)) {
            throw new ValidException("请输入正确的电话号码");
        }
        //电话号码是否已经发送过电话号码
        String key = REDIS_SMS_DRIVER + VerifyCode.Type.LOGIN + ":" + phone;
        if (redisUtil.hasKey(key)) {
            throw new BusinessException("验证码已发送，90秒内不能重复发送");
        }
        redisUtil.put(key, TEST_CODE, 90L, TimeUnit.SECONDS);
        return Result.success().message("验证码发送成功，注意查收");
    }

    @Override
    public Result getInfo(Long id) {
        Driver driver=driverMapper.selectByPrimaryKey(id);
        return Result.success().data(driver);
    }

    @Override
    public Result getCars(Long id) {
        List<DriverCarDTO> driverCarDTO=driverMapper.selectDriverCarsByID(id);
        return Result.success().data(driverCarDTO);
    }

    @Override
    public Result getPublishCar(Long id) {
        DriverCar record=new DriverCar().setDriverId(id).setPublish(CarPublish.START);
        DriverCarDTO driverCarDTO=driverMapper.selectDriverPublishCar(record);
        return Result.success().data(driverCarDTO);
    }

    @Override
    @Transactional
    public Result startCar(Long driverCarId) {
        DriverCar driverCar= driverCarMapper.selectByPrimaryKey(driverCarId);
        Long carId=driverCar.getCarId();
        Long driverId=driverCar.getDriverId();
        //检查该车辆是否发车
        DriverCar isCarPushRecord=new DriverCar().setCarId(carId).setPublish(CarPublish.START);
        DriverCar isCarPush=driverCarMapper.selectOneSelective(isCarPushRecord);
        if(!ObjectUtils.isEmpty(isCarPush)){
            throw new BusinessException("该车辆已经被其它司机发车");
        }
        //检查该司机是否用其它车辆发车
        DriverCar isCarStartRecord=new DriverCar().setDriverId(driverId).setPublish(CarPublish.START);
        DriverCar isCarStart=driverCarMapper.selectOneSelective(isCarStartRecord);
        if (!ObjectUtils.isEmpty(isCarStart)) {
            throw new BusinessException("您已经在发车中，不能再次发车");
        }
        //添加发车的车次，并返回给客户端
        DriverCarBatch driverCarBatchRecord=new DriverCarBatch();
        String batchCode=mUtil.UUID();
        driverCarBatchRecord.setCode(batchCode);
        driverCarBatchRecord.setDriverCarId(driverCarId);
        driverCarBatchMapper.insertSelective(driverCarBatchRecord);
        //发车更新状态
        DriverCar updateRecord=new DriverCar().setId(driverCarId).setDriverId(driverId).setCarId(carId)
                .setState(DriverCarState.IDLE).setPublish(CarPublish.START)
                .setDriverCarBatchId(driverCarBatchRecord.getId());
        driverCarMapper.updateByPrimaryKeySelective(updateRecord);
        return Result.success().message("发车成功").data(driverCarBatchRecord.getId());
    }

    @Override
    public Result stopCar(Long driverCarId) {
        //检查该司机是否在送人过程中
        DriverCar driverCar= driverCarMapper.selectByPrimaryKey(driverCarId);
        //订单正在执行中
        if (driverCar.getState().equals(DriverCarState.RUNNING)){
            throw new BusinessException("车辆正在派送中，不能收车");
        }
        //收车更新状态
        DriverCar updateRecord=new DriverCar().setId(driverCarId).setPublish(CarPublish.STOP);
        driverCarMapper.updateByPrimaryKeySelective(updateRecord);
        //更新车次完成时间
        DriverCarBatch driverCarBatch=new DriverCarBatch()
                .setId(driverCar.getDriverCarBatchId()).setFinishTime(new Date());
        driverCarBatchMapper.updateByPrimaryKeySelective(driverCarBatch);
        return Result.success().message("收车成功");
    }


    @Override
    public Result refreshLocation(DriverCarRefreshVo driverCar) {
        Long driverCarId=driverCar.getId();
        DriverCar db= driverCarMapper.selectByPrimaryKey(driverCarId);
        if(db.getPublish().equals(CarPublish.STOP)){
            throw new BusinessException("收车状态，不能更新坐标");
        }
        driverCarMapper.updateByPrimaryKeySelective(driverCar);
        String lastKey=REDIS_LAST_LOCATION+driverCar.getId()+":"+driverCar.getDriverCarBatchId();
        redisUtil.put(lastKey,driverCar.getLocations());
        return Result.success().message("更新成功");
    }

    @Override
    public Result getNearDriverLocations(BigDecimal lng, BigDecimal lat) {
        Map<String,Object> paramMap=getRangeMap(lng.doubleValue(),lat.doubleValue());
        //只查询当前发车的
        paramMap.put("publish",CarPublish.START);
        List<DriverCar> list= driverCarMapper.selectNearDriverList(paramMap);
        String prefix=REDIS_LAST_LOCATION;
        List<List<Location>> rsList=new ArrayList<>();
        for(DriverCar driverCar:list){
            String key=prefix+driverCar.getId()+":"+driverCar.getDriverCarBatchId();
            if(redisUtil.hasKey(key)){
                List<Location> locations =redisUtil.get(key);
                rsList.add(locations);
            }
        }
        return Result.success().data(rsList);
    }


    private Map<String, Object> getRangeMap(Double longitude, Double latitude) {
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 10;//10千米距离
        double dlng = 2 * Math.asin(Math.sin(dis / (2 * r)) / Math.cos(latitude * Math.PI / 180));
        dlng = dlng * 180 / Math.PI;//角度转为弧度
        double dlat = dis / r;
        dlat = dlat * 180 / Math.PI;
        BigDecimal minlat = new BigDecimal(latitude - dlat).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal maxlat = new BigDecimal(latitude + dlat).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal minlng = new BigDecimal(longitude - dlng).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal maxlng = new BigDecimal(longitude + dlng).setScale(6, BigDecimal.ROUND_HALF_UP);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("minlng", minlng);
        paramMap.put("maxlng", maxlng);
        paramMap.put("minlat", minlat);
        paramMap.put("maxlat", maxlat);
        return paramMap;
    }
}
