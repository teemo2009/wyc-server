package com.ga.wyc.util;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CodeUtil {


    @Value("${jwt.salt}")
    private String salt;

    public String encode(String content, String salt) {
        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher()
                .putString(content, Charsets.UTF_8)
                .putString(salt, Charsets.UTF_8)
                .hash();
        return hc.toString();
    }

    public String encode(String content) {
        return encode(content, salt);
    }
}
