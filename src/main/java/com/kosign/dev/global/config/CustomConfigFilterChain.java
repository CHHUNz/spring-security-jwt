package com.kosign.dev.global.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.kosign.dev.exception.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class CustomConfigFilterChain {
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/public/accounts/**").permitAll()
                .requestMatchers("/api/v1/user/**").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                //.authenticationManager()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                //.authenticationEntryPoint()
                .accessDeniedHandler(new CustomAccessDeniedHandler());
                //.and()
                //.addFilterBefore()
                //.addFilterAfter()
        return httpSecurity.build();


    }
}
