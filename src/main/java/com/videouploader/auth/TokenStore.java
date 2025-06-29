package com.videouploader.auth;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenStore {

    private String accessToken;
    private Instant accessTokenExpiryTime;

    private String refreshToken;

    private String idToken;

    // Set access token and expiry time
    public void setAccessToken(String accessToken, int expiresInSeconds) {
        this.accessToken = accessToken;
        this.accessTokenExpiryTime = Instant.now().plusSeconds(expiresInSeconds - 60); // buffer 1 min
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getAccessTokenExpiryTime() {
        return accessTokenExpiryTime;
    }

    public boolean isAccessTokenExpired() {
        return Instant.now().isAfter(accessTokenExpiryTime);
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void clearTokens() {
        this.accessToken = null;
        this.accessTokenExpiryTime = null;
        this.refreshToken = null;
        this.idToken = null;
    }
}
