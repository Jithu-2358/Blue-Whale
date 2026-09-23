// service/PhotoService.java
package com.bluewhale.service;

import com.bluewhale.model.Photo;
import com.bluewhale.repository.PhotoRepository;
import com.bluewhale.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final FileStorageService storage;

    public PhotoService(PhotoRepository photoRepository, FileStorageService storage) {
        this.photoRepository = photoRepository;
        this.storage = storage;
    }

    @Transactional
    public Photo store(String deviceId, MultipartFile file, String camera) throws IOException {
        String path = storage.storePhoto(file, deviceId);
        return save(deviceId, path, file.getSize(), camera);
    }

    @Transactional
    public Photo store(String deviceId, byte[] data, String camera) throws IOException {
        String path = storage.storePhoto(data, deviceId);
        return save(deviceId, path, data.length, camera);
    }

    private Photo save(String deviceId, String path, long size, String camera) {
        Photo p = new Photo();
        p.setDeviceId(deviceId);
        p.setFilePath(path);
        p.setFileSize(size);
        p.setCapturedAt(Instant.now());
        p.setUploadedAt(Instant.now());
        p.setCamera(camera);
        return photoRepository.save(p);
    }

    public List<Photo> byDevice(String deviceId) {
        return photoRepository.findByDeviceIdOrderByCapturedAtDesc(deviceId);
    }

    public long count(String deviceId) {
        return photoRepository.countByDeviceId(deviceId);
    }
}