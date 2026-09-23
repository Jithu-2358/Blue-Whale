// controller/VideoController.java
package com.bluewhale.controller;

import com.bluewhale.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/metadata")
    public ResponseEntity<?> metadata() {
        var demo = videoService.byType("demo");
        var intro = videoService.byType("intro");
        return ResponseEntity.ok(Map.of(
                "demo", demo.map(v -> Map.of("filename", v.getFilename(), "size", v.getFileSize(), "downloads", v.getDownloadCount())).orElse(null),
                "intro", intro.map(v -> Map.of("filename", v.getFilename(), "size", v.getFileSize(), "downloads", v.getDownloadCount())).orElse(null)
        ));
    }
}