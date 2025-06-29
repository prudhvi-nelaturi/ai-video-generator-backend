package com.videouploader.controller;

import com.videouploader.auth.TokenStore;
import com.videouploader.constants.GoogleOAuthConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private TokenStore tokenStore;

    /**
     * Step 1: Generate Google Authorization URL
     */
    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {
        String authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?scope=https://www.googleapis.com/auth/youtube.upload%20openid%20email%20profile" +
                "&access_type=offline" +
                "&include_granted_scopes=true" +
                "&response_type=code" +
                "&redirect_uri=" + GoogleOAuthConstants.REDIRECT_URI +
                "&client_id=" + GoogleOAuthConstants.CLIENT_ID +
                "&prompt=consent";

        // You can return the URL or directly redirect
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, authorizationUrl)
                .build();
    }

    /**
     * Step 2: Handle OAuth Callback
     */
    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> oauthCallback(@RequestParam("code") String code) {

        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", GoogleOAuthConstants.CLIENT_ID);
        requestBody.add("client_secret", GoogleOAuthConstants.CLIENT_SECRET);
        requestBody.add("redirect_uri", GoogleOAuthConstants.REDIRECT_URI);
        requestBody.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null && responseBody.containsKey("access_token")) {
            String accessToken = responseBody.get("access_token").toString();
            int expiresIn = Integer.parseInt(responseBody.get("expires_in").toString());
            String idToken = responseBody.get("id_token").toString();

            // Handle refresh token safely
            String refreshToken = responseBody.containsKey("refresh_token")
                    ? responseBody.get("refresh_token").toString()
                    : null;

            // Store tokens
            tokenStore.setAccessToken(accessToken, expiresIn);
            tokenStore.setIdToken(idToken);

            if (refreshToken != null) {
                tokenStore.setRefreshToken(refreshToken);
            }

            System.out.println("=== OAuth Token Response ===");
            responseBody.forEach((key, value) -> System.out.println(key + ": " + value));

            return ResponseEntity.ok("Authentication successful! Tokens stored.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication failed. Access token not found.");
        }
    }
}
