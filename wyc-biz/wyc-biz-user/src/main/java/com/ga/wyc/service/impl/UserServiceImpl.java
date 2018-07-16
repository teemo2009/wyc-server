package com.ga.wyc.service.impl;

import com.ga.wyc.RedisUtil;
import com.ga.wyc.dao.UserMapper;
import com.ga.wyc.domain.bean.BusinessException;
import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.ValidException;
import com.ga.wyc.domain.bean.VerifyCode;
import com.ga.wyc.domain.dto.UserDTO;
import com.ga.wyc.domain.entity.User;
import com.ga.wyc.service.IUserService;
import com.ga.wyc.util.CodeUtil;
import com.ga.wyc.util.JWTUtil;
import com.ga.wyc.util.MUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {
    @Resource
    UserMapper userMapper;
    @Resource
    MUtil mUtil;
    @Resource
    RedisUtil redisUtil;
    @Resource
    JWTUtil jwtUtil;
    @Resource
    CodeUtil codeUtil;

    @Value("${redis.sms}")
    private String REDIS_SMS;

    @Value("${redis.user}")
    private String REDIS_USER;

    @Value("${redis.testCode}")
    private String TEST_CODE;

    @Override
    public Result login(String phone, String code) {
        //电话号码匹配
        if (!mUtil.isTelphone(phone)) {
            throw new ValidException("请输入正确的电话号码");
        }
        //判断是否发送了验证码
        String smsKey = REDIS_SMS + VerifyCode.Type.LOGIN + ":" + phone;
        if (!redisUtil.hasKey(smsKey)) {
            throw new BusinessException("验证码失效");
        }
        String cacheCode = redisUtil.get(smsKey);
        if (!cacheCode.equals(code)) {
            throw new BusinessException("验证码不匹配");
        }
        //判断数据库中是否有注册
        User hasPhoneRs = userMapper.selectOneSelective(new User().setPhone(phone));
        Long userId = 0L;
        if (ObjectUtils.isEmpty(hasPhoneRs)) {
            //新的用户，先注册一次
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setCode(mUtil.UUID());
            userMapper.insertSelective(newUser);
            userId = newUser.getId();
        } else {
            userId = hasPhoneRs.getId();
        }
        //登陆成功生成token
        String token = jwtUtil.sign(phone);
        String refreshToken = codeUtil.encode(token);
        UserDTO userDTO = new UserDTO();
        userDTO.setToken(token);
        userDTO.setRefreshToken(refreshToken);
        userDTO.setId(userId);
        userDTO.setPhone(phone);
        String userKey = REDIS_USER + phone;
        redisUtil.put(userKey, userDTO);
        //删除短信验证码的缓存
        redisUtil.remove(smsKey);
        return Result.success().message("登陆成功").data(userDTO);
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
        String key = REDIS_SMS + VerifyCode.Type.LOGIN + ":" + phone;
        if (redisUtil.hasKey(key)) {
            throw new BusinessException("验证码已发送，3分钟内不能重复发送");
        }
        redisUtil.put(key, TEST_CODE, 3L, TimeUnit.MINUTES);
        return Result.success().message("验证码发送成功，注意查收");
    }
}
