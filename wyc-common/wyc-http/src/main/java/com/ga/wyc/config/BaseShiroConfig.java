package com.ga.wyc.config;

import com.ga.wyc.domain.bean.StatelessAuthcFilter;
import com.ga.wyc.domain.bean.StatelessDefaultSubjectFactory;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;
import java.util.*;

/**
 *  一个工程只能实现其一个子类
 * */
public abstract class BaseShiroConfig  {

    /**子类需要覆盖该实现
     * @return
     */
    @Bean("customRealm")
    public abstract BaseCustomRealm customRealm();


    /**
     * 匿名访问的url(不需要拦截的)
     *
     * @return
     */
    public  abstract List<String> filterByAnon();


    @Bean
    public StatelessDefaultSubjectFactory subjectFactory() {
        return new StatelessDefaultSubjectFactory();
    }

    @Bean
    public DefaultSessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("customRealm") BaseCustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        securityManager.setSubjectFactory(subjectFactory());
        securityManager.setSessionManager(sessionManager());
        DefaultSubjectDAO dao = (DefaultSubjectDAO) securityManager.getSubjectDAO();
        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = (DefaultWebSessionStorageEvaluator) dao.getSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return securityManager;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("statelessAuthc", new StatelessAuthcFilter());
        factoryBean.setFilters(filterMap);
        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        //不需要拦截的请求过滤
        for (String s : filterByAnon()) {
            filterChainDefinitionMap.put(s, "anon");
        }
        //所有的请求都需要拦截
        filterChainDefinitionMap.put("/**", "statelessAuthc");
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}