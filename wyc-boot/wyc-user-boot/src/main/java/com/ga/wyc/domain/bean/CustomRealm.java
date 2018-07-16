package com.ga.wyc.domain.bean;

import com.ga.wyc.RedisUtil;
import com.ga.wyc.config.BaseCustomRealm;
import com.ga.wyc.domain.dto.UserDTO;
import com.ga.wyc.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

@Slf4j
public class CustomRealm extends BaseCustomRealm {

    @Resource
    JWTUtil jwtUtil;

    @Autowired
    RedisUtil redisUtil;

    @Value("${redis.user}")
    private String REDIS_USER;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //权限授权
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //认证登录
        StatelessToken statelessToken = (StatelessToken) token;
        String phone = statelessToken.getUsername();
        String clientDigest = statelessToken.getClientDigest();
        if (!jwtUtil.verify(phone, clientDigest)) {
            return null;
        }
        String key = REDIS_USER + phone;
        //判断缓存中是否有登陆信息
        if (redisUtil.hasKey(key)) {
            //判断token是否一致
           UserDTO userDTO = redisUtil.get(key);
            if (!userDTO.getToken().equals(clientDigest)) {
                log.info("异地登录");
                throw new DisabledAccountException();
            }
            return new SimpleAuthenticationInfo(userDTO, clientDigest, this.getName());
        } else {
            //没有登录
            return null;
        }

    }
}
