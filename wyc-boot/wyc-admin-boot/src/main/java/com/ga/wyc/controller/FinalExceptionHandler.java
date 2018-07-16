package com.ga.wyc.controller;

import com.ga.wyc.domain.bean.Result;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class FinalExceptionHandler extends AbstractErrorController {

    public FinalExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @GetMapping(value = "/error")
    public Object error(HttpServletResponse resp, HttpServletRequest req) {
        HttpStatus status =this.getStatus(req);
        Map<String, Object> errAttr= this.getErrorAttributes(req,true);
        String rs="";
       for (Map.Entry<String,Object> entry:errAttr.entrySet()){
           rs+="->"+entry.getValue();
       }
        return Result.custom().code(status.value()).message(rs);
    }
}
