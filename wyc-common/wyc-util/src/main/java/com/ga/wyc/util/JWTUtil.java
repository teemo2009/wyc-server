package com.ga.wyc.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;


@Component
public class JWTUtil {
    private static final String SECRET = "XX#$%()(#*!()!KL<><MQLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";
    private Algorithm algorithm = Algorithm.HMAC256(SECRET);
    /**
     * 单位是秒
     */
    @Value("${jwt.expire}")
    private int expire;

    /**
     * 签名生成jwt
     *
     * @param content
     * @return 生成失败，返回 ""
     */
    public String sign(String content) {
        try {
            DateTime dateTime = DateTime.now().plusSeconds(expire);
            return JWT.create()
                    .withClaim("content", content)
                    .withClaim("date", System.currentTimeMillis())
                    .withExpiresAt(dateTime.toDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        }
        return "";
    }

    /**校验jwt
     * @param content
     * @param token
     * @return 过期，异常都返回false
     */
    public boolean verify(String content, String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expiresAt = jwt.getExpiresAt();
            if (new DateTime(expiresAt).isAfterNow()) {
                return Objects.equals(jwt.getClaim("content").asString(), content);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

}
