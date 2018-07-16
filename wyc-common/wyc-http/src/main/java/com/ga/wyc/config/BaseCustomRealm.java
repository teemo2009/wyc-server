package com.ga.wyc.config;

import com.ga.wyc.domain.bean.StatelessToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *  realm基类
 * */
public abstract class BaseCustomRealm extends AuthorizingRealm {
    //设置realm的名称
    @Override
    public boolean supports(AuthenticationToken token) {
        //仅支持StatelessToken类型的Token
        return token instanceof StatelessToken;
    }
    @Override
    protected abstract AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals);
    @Override
    protected abstract AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;
}
