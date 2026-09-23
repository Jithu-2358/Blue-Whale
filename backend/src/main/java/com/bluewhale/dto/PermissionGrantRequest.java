// dto/PermissionGrantRequest.java
package com.bluewhale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionGrantRequest {
    @NotBlank
    private String deviceId;
    @NotBlank
    private String permission;
    private boolean granted;
}