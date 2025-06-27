package com.videouploader.controller;

import com.videouploader.constants.GoogleOAuthConstants;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AuthController {


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
        ResponseEntity<String> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return ResponseEntity.ok("Access Token Response: " + response.getBody());
    }
}


