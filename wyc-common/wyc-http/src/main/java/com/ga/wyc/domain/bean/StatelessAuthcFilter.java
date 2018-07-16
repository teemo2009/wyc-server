package com.ga.wyc.domain.bean;

import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessAuthcFilter extends AccessControlFilter {

    private final String PARAM_TOKEN="Access-token";
    private final String PARAM_DIGEST="Client-digest";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if ("OPTIONS".equals(httpServletRequest.getMethod())) {
            return true;
        }
        //1、客户端生成的消息摘要
        String accessToken = httpServletRequest.getHeader(PARAM_TOKEN);
        //2、客户端传入的用户身份
        String username = httpServletRequest.getHeader(PARAM_DIGEST);
        if(ObjectUtils.isEmpty(accessToken)||ObjectUtils.isEmpty(username)){
            //登录失败
            onLoginFail(response);
            return false;
        }
        //4、生成无状态Token
        StatelessToken token = new StatelessToken(username, accessToken);
        try {
            //5、委托给Realm进行登录
            getSubject(request, response).login(token);
        } catch (Exception e) {
            if(e instanceof DisabledAccountException){
                //远程异常登录
                onRemoteFail(response);
            }else{
                //6、登录失败
                onLoginFail(response);
            }
            return false;
        }
        return true;
    }

    /**
     * 登录失败时默认返回401状态码
     *
     * @param response
     * @throws IOException
     */
    private void onLoginFail(ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-type", "text/html;charset=UTF-8");
        httpResponse.setCharacterEncoding("utf-8");
        wrapCorsResponse(httpResponse);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write("登录异常");
    }
    /**
     * 异地时默认返回4010状态码
     *
     * @param response
     * @throws IOException
     */
    private void onRemoteFail(ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-type", "text/html;charset=UTF-8");
        httpResponse.setCharacterEncoding("utf-8");
        wrapCorsResponse(httpResponse);
        httpResponse.setStatus(4010);
        httpResponse.getWriter().write("账号在异地登录");
    }



    /**
     * 添加cors支持
     *
     * @param response
     */
    private void wrapCorsResponse(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.addHeader("Access-Control-Max-Age", "1800");
    }
}