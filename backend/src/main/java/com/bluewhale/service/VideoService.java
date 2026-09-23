// service/VideoService.java
package com.bluewhale.service;

import com.bluewhale.model.Video;
import com.bluewhale.repository.VideoRepository;
import com.bluewhale.storage.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final FileStorageService storage;

    public VideoService(VideoRepository videoRepository, FileStorageService storage) {
        this.videoRepository = videoRepository;
        this.storage = storage;
    }

    @PostConstruct
    public void registerKnownVideos() {
        register("blue-whale-demo.mp4", "demo");
        register("blue-whale-intro.mp4", "intro");
    }

    private void register(String filename, String type) {
        Path p = storage.videoPath(filename);
        try {
            if (Files.exists(p) && videoRepository.findByFilename(filename).isEmpty()) {
                Video v = new Video();
                v.setFilename(filename);
                v.setFilePath(p.toString());
                v.setFileSize(Files.size(p));
                v.setType(type);
                v.setDownloadCount(0);
                videoRepository.save(v);
            }
        } catch (Exception ignored) { }
    }

    @Transactional
    public void incrementDownload(String filename) {
        videoRepository.findByFilename(filename).ifPresent(v -> {
            v.setDownloadCount(v.getDownloadCount() + 1);
            videoRepository.save(v);
        });
    }

    public Optional<Video> byType(String type) {
        return videoRepository.findByType(type);
    }
}