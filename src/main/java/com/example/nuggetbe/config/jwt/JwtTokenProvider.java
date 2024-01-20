package com.example.nuggetbe.config.jwt;

import org.springframework.security.core.Authentication;

public class JwtTokenProvider {
    public boolean validToken(String token) {
        return false;
    }

    public Authentication getAuthentication(String token) {
        return null;
    }
}
