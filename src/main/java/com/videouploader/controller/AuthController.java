package com.videouploader.controller;

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

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

//    spring.security.oauth2.client.registration.google.client-id=
//    spring.security.oauth2.client.registration.google.client-secret=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
//    spring.security.oauth2.client.registration.google.scope=openid, profile, email, https://www.googleapis.com/auth/youtube.upload
//    spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/oauth2/callback
//    spring.security.oauth2.client.registration.google.authorization-grant-type=authorization_code
//    spring.security.oauth2.client.registration.google.client-name=Google


    private static final String CLIENT_ID = "464197801957-4b77jjl3kkqu2mnqqaupo3ahe73sa57i.apps.googleusercontent.com";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth2/callback";
    private static final String SCOPE = "https://www.googleapis.com/auth/youtube.upload";
    private static final String CLIENT_SECRET = "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";

    @GetMapping("/authorize")
    public RedirectView authorize() {
        String authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_type=code" +
                "&scope=" + SCOPE +
                "&access_type=offline" +
                "&prompt=consent";

        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> oauthCallback(@RequestParam("code") String code) {

        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        // ✅ Prepare the request body using MultiValueMap
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("redirect_uri", REDIRECT_URI);
        requestBody.add("grant_type", "authorization_code");

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare the request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request to Google's token endpoint
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // You will receive access_token and refresh_token in the response body
        return ResponseEntity.ok("Access Token Response: " + response.getBody());
    }

    @GetMapping("/success")
    public String success(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                          @AuthenticationPrincipal OAuth2User oauth2User) {

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        System.out.println("Access Token: " + accessToken);

        return "Login successful! Access Token: " + accessToken;
    }
}


