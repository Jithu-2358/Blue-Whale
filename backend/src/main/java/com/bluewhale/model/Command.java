package com.bluewhale.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "commands")
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String deviceId;

    @Column(nullable = false, length = 64)
    private String command;

    @Column(columnDefinition = "TEXT")
    private String params;

    private Instant issuedAt;
    private String status = "pending";

    @Column(columnDefinition = "TEXT")
    private String result;
}