package com.bluewhale.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    private long fileSize;
    private String type; // demo / intro
    private String metadata;
    private int downloadCount = 0;
}