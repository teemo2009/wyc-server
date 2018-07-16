package com.ga.wyc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/***
 * @author luqi
 * redis抽象类
 * */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    protected RedisTemplate redisTemplate;

    @Value("${redis.prefix}")
    protected String prefix;

    public String perfixFomatter(String key) {
        return this.prefix + key;
    }

    /**
     * 添加
     *
     * @param key
     * @param value
     */
    public <T> void put(String key, T value) {
        redisTemplate.opsForValue().set(generateKey(key), value);
    }

    /**
     * 添加
     *
     * @param key
     * @param value
     * @param expire (默认为秒)
     */
    public <T> void put(String key, T value, long expire) {
        redisTemplate.opsForValue().set(generateKey(key), value, expire, TimeUnit.SECONDS);
    }

    /**
     * 添加
     *
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     */
    public <T> void put(String key, T value, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(generateKey(key), value, expire, timeUnit);
    }


    /**
     * 删除
     */
    public boolean remove(String key) {
        try {
            redisTemplate.opsForValue().getOperations().delete(generateKey(key));
            return true;
        } catch (Throwable t) {
            log.error("redis remove " + key + " has error,detail----" + t);
        }
        return false;
    }

    /**
     * 批量删除value通过key的表达式
     */
    public boolean removeMatch(String match) {
        Set<String> set = redisTemplate.keys(generateKey(match));
        Iterator<String> it = set.iterator();
        if (it.hasNext()) {
            String keyStr = it.next();
            try {
                redisTemplate.delete(keyStr);
            } catch (Throwable t) {
                log.error("redis remove " + keyStr + " has error,detail----" + t);
                return false;
            }
        }
        return true;
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(generateKey(key));
        } catch (Throwable t) {
            log.error("redis has key exec was error " + key + ", detail----" + t);
        }
        return false;
    }


    /**
     * 获取
     */
    public <T> T get(String key) {
        try {
            return (T) redisTemplate.opsForValue().get(generateKey(key));
        } catch (Throwable t) {
            log.error("redis get key exec was error " + key + ", detail----" + t);
        }
        return null;
    }

    private String generateKey(String key) {
        return getKeyPrefix() + key;
    }

    protected String getKeyPrefix() {
        return "";
    }

}
