package com.blackjack.jraoms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        //Get user details form spring security's context holder
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        String name = userDetails.getUserName();
        String email = userDetails.getUsername();
        int id = userDetails.getUserId();
        Set<String> role = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (role.contains("ADMIN")) {
            //Logging
            log.info("Login User - " + name + "." + role);
            response.sendRedirect("/dashboard?AdminLoginSuccess");
        }else {
            //Logging
            log.info("Login User - " + name + "." + role);
            response.sendRedirect("/dashboard?LoginSuccess");
        }
    }
}