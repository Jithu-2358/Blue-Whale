// controller/PhotoController.java
package com.bluewhale.controller;

import com.bluewhale.mapper.PhotoMapper;
import com.bluewhale.model.Photo;
import com.bluewhale.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /** APK uploads photo (multipart) — POST /api/v1/photo/upload */
    @PostMapping("/photo/upload")
    public ResponseEntity<?> upload(@RequestParam("deviceId") String deviceId,
                                    @RequestParam("photo") MultipartFile photo,
                                    @RequestParam(value = "camera", defaultValue = "back") String camera) {
        try {
            Photo p = photoService.store(deviceId, photo, camera);
            return ResponseEntity.ok(Map.of("status", "ok", "photoId", p.getId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** APK uploads photo (base64 JSON) — POST /api/v1/photo/upload-base64 */
    @PostMapping("/photo/upload-base64")
    public ResponseEntity<?> uploadBase64(@RequestBody Map<String, String> body) {
        String deviceId = body.get("deviceId");
        String photoB64 = body.get("photo");
        String camera = body.getOrDefault("camera", "back");
        try {
            byte[] data = Base64.getDecoder().decode(photoB64);
            Photo p = photoService.store(deviceId, data, camera);
            return ResponseEntity.ok(Map.of("status", "ok", "photoId", p.getId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/photos/{deviceId}")
    public ResponseEntity<?> photos(@PathVariable String deviceId) {
        List<Map<String, Object>> list = photoService.byDevice(deviceId).stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "deviceId", p.getDeviceId(),
                        "url", "/photos/" + p.getFilePath().substring(p.getFilePath().lastIndexOf('/') + 1),
                        "fileSize", p.getFileSize(),
                        "capturedAt", p.getCapturedAt() != null ? p.getCapturedAt().toString() : null,
                        "camera", p.getCamera()))
                .toList();
        return ResponseEntity.ok(Map.of("photos", list));
    }

    @GetMapping("/photos/{deviceId}/count")
    public ResponseEntity<?> count(@PathVariable String deviceId) {
        return ResponseEntity.ok(Map.of("count", photoService.count(deviceId)));
    }
}