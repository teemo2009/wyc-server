package com.ga.wyc.handle;

import com.ga.wyc.domain.bean.BusinessException;
import com.ga.wyc.domain.bean.Result;
import com.ga.wyc.domain.bean.TokenLoseException;
import com.ga.wyc.domain.bean.ValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
@RestController
@Slf4j
public class ExceptionHandle extends ResponseEntityExceptionHandler  {

    /**
     * 在controller里面内容执行之前，校验一些参数不匹配啊，Get post方法不对啊之类的
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity(ex.getMessage(), status);
    }


    /**
     *  进入业务逻辑中出错
     * */
    @ExceptionHandler(value = Exception.class)
    public Result handle(Exception e){
        if(e instanceof ValidException){
            ValidException validException= (ValidException) e;
            //验证参数错误
            return  Result.custom().code(validException.getCode())
                    .message(validException.getMessage()+"，"+validException.getDetail());
        } else if(e instanceof BusinessException){
            BusinessException businessException= (BusinessException) e;
            //业务逻辑异常
            return Result.fail().message(businessException.getDetail());
        } else if(e instanceof TokenLoseException){
            //token异常
            TokenLoseException tokenLoseException= (TokenLoseException) e;
            return Result.custom().code(tokenLoseException.getCode())
                    .message(tokenLoseException.getMessage());
        }
        String msg=getExceptionToString(e);
        log.error("class:{},detail:{}",e.getClass().getName(),msg);
        return Result.unkonw().message(msg);
    }


    /**
     * 将 Exception 转化为 String
     */
    private String getExceptionToString(Throwable e) {
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }


}
