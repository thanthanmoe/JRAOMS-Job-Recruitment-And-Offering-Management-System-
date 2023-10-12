package com.blackjack.jraoms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/assets/**","/forms/**","/ws/**").permitAll()
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/","/jobs/**","/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/manage/**","/senior/**").hasAnyAuthority("SENIOR", "ADMIN")
                        .requestMatchers("/vacancy/**").hasAnyAuthority("JUNIOR","SENIOR","ADMIN")
                        .requestMatchers("/interviewer/**","/interview/**").hasAuthority("INTERVIEWER")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(successHandler())
                        .usernameParameter("email")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logoutsuccess")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}