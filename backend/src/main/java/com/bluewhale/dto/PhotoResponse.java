// dto/PhotoResponse.java
package com.bluewhale.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class PhotoResponse {
    private Long id;
    private String deviceId;
    private String photoUrl;
    private long fileSize;
    private Instant capturedAt;
    private String camera;
}