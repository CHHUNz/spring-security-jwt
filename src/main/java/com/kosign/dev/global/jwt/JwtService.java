package com.kosign.dev.global.jwt;

import com.kosign.dev.service.user.CustomUserDetail;
import io.jsonwebtoken.Claims;

import java.security.Key;

public interface JwtService {
    Claims extractClaims(String token);
    Key getKey();
    String generateToken(CustomUserDetail customUserDetail);
    String refreshToken(CustomUserDetail customUserDetail);
    boolean isValidToken(String token);

}
