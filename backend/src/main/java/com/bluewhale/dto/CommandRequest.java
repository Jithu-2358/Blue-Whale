// dto/CommandRequest.java
package com.bluewhale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommandRequest {
    @NotBlank
    private String deviceId;
    @NotBlank
    private String command;
    private String params = "{}";
}