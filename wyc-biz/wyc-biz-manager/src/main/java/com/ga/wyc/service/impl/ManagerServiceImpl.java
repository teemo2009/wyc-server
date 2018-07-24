package com.ga.wyc.service.impl;

import com.ga.wyc.RedisUtil;
import com.ga.wyc.dao.ManagerMapper;
import com.ga.wyc.domain.bean.BusinessException;
import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.TokenLoseException;
import com.ga.wyc.domain.dto.ManagerDTO;
import com.ga.wyc.domain.entity.Manager;
import com.ga.wyc.service.IManagerService;
import com.ga.wyc.util.CodeUtil;
import com.ga.wyc.util.JWTUtil;
import com.ga.wyc.util.MUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("managerService")
public class ManagerServiceImpl implements IManagerService {
    @Resource
    MUtil mUtil;
    @Resource
    RedisUtil redisUtil;
    @Resource
    JWTUtil jwtUtil;
    @Resource
    CodeUtil codeUtil;
    @Resource
    ManagerMapper managerMapper;

    @Value("${redis.manager}")
    String REDIS_MANAGER;

    @Override
    public Result add(Manager manager) {
        Manager hasUserName=managerMapper.selectOneSelective(new Manager().setUserName(manager.getUserName()));
        if(!ObjectUtils.isEmpty(hasUserName)){
            throw new BusinessException("用户名已经注册");
        }
        Manager hasPhone=managerMapper.selectOneSelective(new Manager().setUserName(manager.getPhone()));
        if(!ObjectUtils.isEmpty(hasPhone)){
            throw new BusinessException("电话已经注册");
        }
        String md5Pwd=mUtil.MD5(manager.getPassword());
        manager.setPassword(md5Pwd);
        managerMapper.insertSelective(manager);
        return Result.success().message("操作成功");
    }

    @Override
    public Result login(Manager manager) {
        Manager hasManager= managerMapper.selectOneSelective(manager);
        if(ObjectUtils.isEmpty(hasManager)){
            throw new BusinessException("用户名或密码错误");
        }
        //生成token
        String token = jwtUtil.sign(hasManager.getUserName());
        String refreshToken = codeUtil.encode(token);
        ManagerDTO managerDTO=new ManagerDTO();
        BeanUtils.copyProperties(hasManager,managerDTO,"password");
        managerDTO.setToken(token).setRefreshToken(refreshToken);
        String managerKey = REDIS_MANAGER + managerDTO.getUserName();
        //登陆后记录于缓存
        redisUtil.put(managerKey, managerDTO);
        return Result.success().message("登陆成功").data(managerDTO);
    }


    @Override
    public Result refreshToken(String userName, String token, String refreshToken) {
        if (!refreshToken.equals(codeUtil.encode(token))) {
            throw new BusinessException("refresh_token校验失败");
        }
        if (!jwtUtil.verify(userName, token)) {
            throw new BusinessException("token校验失败");
        }
        token = jwtUtil.sign(userName);
        refreshToken = codeUtil.encode(token);
        String managerKey = REDIS_MANAGER + userName;
        //判断token是否异常
        if(!redisUtil.hasKey(managerKey)){
            throw new TokenLoseException();
        }
        ManagerDTO managerDTO= redisUtil.get(managerKey);
        managerDTO.setToken(token);
        managerDTO.setRefreshToken(refreshToken);
        //刷新缓存中的token
        redisUtil.put(managerKey,managerDTO);
        Map map = new HashMap(2);
        map.put("token", token);
        map.put("refreshToken", refreshToken);
        return Result.success().data(map);
    }
}
