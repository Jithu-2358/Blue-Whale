package com.bluewhale.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String deviceId;

    @Column(nullable = false)
    private String filePath;

    private long fileSize;
    private Instant capturedAt;
    private Instant uploadedAt;
    private String camera = "back";
}