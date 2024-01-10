package com.kosign.dev.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosign.dev.payload.authentication.AuthenticationRequest;
import com.kosign.dev.payload.authentication.AuthenticationResponse;
import com.kosign.dev.service.user.CustomUserDetail;
import com.kosign.dev.service.user.CustomUserDetailService;
import com.kosign.dev.utils.CustomMessageExceptionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collections;
@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private ObjectMapper objectMapper;
    private JwtService jwtService;
    private CustomUserDetailService customUserDetailService;
    public JwtAuthenticationFilter(ObjectMapper objectMapper,
                                   JwtService jwtService,
                                   JwtConfig jwtConfig,
                                   CustomUserDetailService customUserDetailService,
                                   AuthenticationManager authenticationManager){
        super(new AntPathRequestMatcher(jwtConfig.getUrl(), "POST"));
        setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        this.customUserDetailService = customUserDetailService;

    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        logger.info("Attempt Authentication");
        AuthenticationRequest authenticationRequest = objectMapper.readValue(
                request.getInputStream(), AuthenticationRequest.class
        );
        customUserDetailService.saveUserAttemptAuthentication(authenticationRequest.username());

        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.username(),
                authenticationRequest.password(),
                Collections.emptyList()
        ));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetail customUserDetail = (CustomUserDetail) authResult.getPrincipal();
        var accessToken = jwtService.generateToken(customUserDetail);
        var refreshToken = jwtService.refreshToken(customUserDetail);
        customUserDetailService.updateLoginAttempt(customUserDetail.getUsername());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(
                accessToken,
                refreshToken
        );
        var authzJson = objectMapper.writeValueAsString(authenticationResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(authzJson);
        log.info("Successfully Authentication {}", authzJson);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        var msgException = CustomMessageExceptionUtils.unauthorized();
        var msgJson = objectMapper.writeValueAsString(msgException);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(msgJson);
        log.warn("Unsuccessfully Authentication {}", msgJson);

    }
}
