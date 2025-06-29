package com.videouploader.service;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import org.springframework.stereotype.Service;
import com.google.api.client.util.DateTime;

import java.io.InputStream;
import java.util.Collections;

@Service
public class YouTubeService {

    public String uploadVideo(YouTube youtubeService,
                              InputStream videoStream,
                              String fileName,
                              String title,
                              String description,
                              String privacyStatus,
                              String publishAt) throws Exception {

        Video videoObjectDefiningMetadata = new Video();

        // Set metadata
        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        snippet.setTags(Collections.singletonList("AI Generated"));

        // If scheduling is requested
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(privacyStatus); // "private", "public", "unlisted"

        if (publishAt != null && !publishAt.isEmpty()) {
            status.setPrivacyStatus("private");
            status.setPublishAt(new DateTime(publishAt)); // RFC 3339 format required
        }

        videoObjectDefiningMetadata.setSnippet(snippet);
        videoObjectDefiningMetadata.setStatus(status);

        AbstractInputStreamContent mediaContent = new InputStreamContent("video/*", videoStream);

        YouTube.Videos.Insert request = youtubeService.videos()
                .insert("snippet,status", videoObjectDefiningMetadata, mediaContent);

        Video response = request.execute();

        return "https://www.youtube.com/watch?v=" + response.getId();
    }
}

