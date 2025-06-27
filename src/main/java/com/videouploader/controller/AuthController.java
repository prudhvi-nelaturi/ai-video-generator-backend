package com.videouploader.controller;

import com.google.api.client.util.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AuthController {

    private static final String CLIENT_ID = "464197801957-4b77jjl3kkqu2mnqqaupo3ahe73sa57i.apps.googleusercontent.com";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth2/callback";
    private static final String SCOPE = "https://www.googleapis.com/auth/youtube.upload";
    private static final String CLIENT_SECRET = "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";

    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> oauthCallback(@RequestParam("code") String code) {

        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("redirect_uri", REDIRECT_URI);
        requestBody.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return ResponseEntity.ok("Access Token Response: " + response.getBody());
    }
}


