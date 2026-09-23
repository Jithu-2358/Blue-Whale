// dto/CommandResponse.java
package com.bluewhale.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class CommandResponse {
    private Long id;
    private String deviceId;
    private String command;
    private String params;
    private Instant issuedAt;
    private String status;
    private String result;
}