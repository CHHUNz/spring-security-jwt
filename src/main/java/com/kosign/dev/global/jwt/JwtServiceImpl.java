package com.kosign.dev.global.jwt;

import com.kosign.dev.exception.CustomMessageException;
import com.kosign.dev.service.user.CustomUserDetail;
import com.kosign.dev.service.user.CustomUserDetailService;
import com.kosign.dev.utils.Constant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl extends JwtConfig implements JwtService {
    private final CustomUserDetailService customUserDetailService;
    @Override
    public Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Key getKey() {
        byte[] keys = Decoders.BASE64.decode(getSecret());
        return Keys.hmacShaKeyFor(keys);
    }

    @Override
    public String generateToken(CustomUserDetail customUserDetail) {
        List<String> roles = new ArrayList<>();
        customUserDetail.getAuthorities().forEach(role ->
                roles.add(role.getAuthority()));
        Instant currentDateTime = Instant.now();
        return Jwts.builder()
                .setSubject(customUserDetail.getUsername())
                .claim("authorities", customUserDetail.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("roles", roles)
                .claim("isEnable", customUserDetail.isEnabled())
                .setIssuedAt(Date.from(currentDateTime.plusSeconds(getExpiration())))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String refreshToken(CustomUserDetail customUserDetail) {
        Instant currentDateTime = Instant.now();
        return Jwts.builder()
                .setSubject(customUserDetail.getUsername())
                .claim("authorities", customUserDetail.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("isEnable", customUserDetail.isEnabled())
                .setIssuedAt(Date.from(currentDateTime.plusSeconds(getExpiration())))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isValidToken(String token) {
        final String username = extractUsernameByToken(token);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        return userDetails != null;
    }

    private String extractUsernameByToken(String token) {
        return extractClaimsFunction(token, Claims::getSubject);
    }

    private <T> T extractClaimsFunction(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractClaimsByToken(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractClaimsByToken(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupport JWT token {}", e.getLocalizedMessage());
            throw new CustomMessageException("Token Unsupport", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        } catch (SignatureException | MalformedJwtException e) {
            log.error("Token is invalid format {}", e.getLocalizedMessage());
            throw new CustomMessageException("Token is invalid format {}", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        } catch (ExpiredJwtException e) {
            log.error("Token is expired {}", e.getLocalizedMessage());
            throw new CustomMessageException("Token is expired {}", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            log.error("{}", e.getLocalizedMessage());
            throw new CustomMessageException("Unknown error", String.valueOf(HttpStatus.UNAUTHORIZED.value()));

        }
    }
}
