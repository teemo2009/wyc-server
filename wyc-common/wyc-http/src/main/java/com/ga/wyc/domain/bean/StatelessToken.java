package com.ga.wyc.domain.bean;

import org.apache.shiro.authc.AuthenticationToken;

public class StatelessToken implements AuthenticationToken {
    private String username;
    private String clientDigest;

    public StatelessToken(String username, String clientDigest) {
        this.username = username;
        this.clientDigest = clientDigest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientDigest() {
        return clientDigest;
    }

    public void setClientDigest(String clientDigest) {
        this.clientDigest = clientDigest;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        return this.clientDigest;
    }
}
