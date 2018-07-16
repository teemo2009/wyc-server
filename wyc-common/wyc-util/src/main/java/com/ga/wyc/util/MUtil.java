package com.ga.wyc.util;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MUtil {
    /**
     * MD5
     * @param md5
     * @return 加密字符串
     */
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * @author luqi
     * 是否为电话
     */
    public boolean isTelphone(String telphone) {
        return isMatchReg(telphone, "^[1][3,4,5,6,7,8,9][0-9]{9}$");
    }

    /**
     * @author luqi
     * 正则表达式是否匹配
     */
    public boolean isMatchReg(String msg, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(msg);
        return matcher.matches();
    }


    public String UUID(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }
}
