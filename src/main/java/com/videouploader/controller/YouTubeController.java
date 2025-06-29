package com.videouploader.controller;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.videouploader.auth.TokenStore;
import com.videouploader.service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/youtube")
public class YouTubeController {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private YouTubeService youTubeService;

    @PostMapping("/upload")
    public String uploadVideo(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam(defaultValue = "private") String privacyStatus,
                              @RequestParam(required = false) String publishAt) {
        try {
            String accessToken = tokenStore.getAccessToken();

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            YouTube youtubeService = new YouTube.Builder(httpTransport, jsonFactory, request -> {
                request.getHeaders().setAuthorization("Bearer " + accessToken);
            }).setApplicationName("AI Video Uploader").build();

            InputStream videoInputStream = file.getInputStream();

            String videoUrl = youTubeService.uploadVideo(
                    youtubeService,
                    videoInputStream,
                    file.getOriginalFilename(),
                    title,
                    description,
                    privacyStatus,
                    publishAt
            );

            return "Video uploaded successfully: " + videoUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload video: " + e.getMessage();
        }
    }
}
