package com.blackjack.jraoms.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorPageController implements ErrorController {
    @RequestMapping("/error")
    public String errorPage(HttpServletRequest request){
        Object errorStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (errorStatus != null){
            Integer statusCode = Integer.valueOf(errorStatus.toString());

            if (statusCode.equals(HttpStatus.BAD_REQUEST.value())){
                return "/error/pages-error-400";
            }else if (statusCode.equals(HttpStatus.FORBIDDEN.value())){
                return "/error/pages-error-403";
            }else if (statusCode.equals(HttpStatus.NOT_FOUND.value())){
                return "/error/pages-error-404";
            }else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR.value())){
                return "/error/pages-error-500";
            }
        }
        return "/error/default-error";
    }
}