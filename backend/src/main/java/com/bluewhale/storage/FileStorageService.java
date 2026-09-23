// storage/FileStorageService.java
package com.bluewhale.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final StorageProperties props;

    public FileStorageService(StorageProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(props.getVideoPath()));
        Files.createDirectories(Paths.get(props.getPhotoPath()));
        Files.createDirectories(Paths.get(props.getApkPath()));
    }

    public String storePhoto(MultipartFile file, String deviceId) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String filename = deviceId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path dest = Paths.get(props.getPhotoPath()).resolve(filename);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        return dest.toString();
    }

    public String storePhoto(byte[] data, String deviceId) throws IOException {
        String filename = deviceId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        Path dest = Paths.get(props.getPhotoPath()).resolve(filename);
        Files.write(dest, data);
        return dest.toString();
    }

    public Path photoPath(String filename) {
        return Paths.get(props.getPhotoPath()).resolve(filename);
    }

    public Path videoPath(String filename) {
        return Paths.get(props.getVideoPath()).resolve(filename);
    }

    public Path apkPath(String filename) {
        return Paths.get(props.getApkPath()).resolve(filename);
    }

    public long sizeOf(String path) {
        try { return Files.size(Paths.get(path)); } catch (IOException e) { return 0; }
    }

    private String getExtension(String name) {
        if (name == null) return ".jpg";
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i) : ".jpg";
    }
}