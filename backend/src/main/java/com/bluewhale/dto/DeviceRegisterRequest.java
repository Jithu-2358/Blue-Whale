// dto/DeviceRegisterRequest.java
package com.bluewhale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRegisterRequest {
    @NotBlank
    private String deviceId;
    private String deviceName;
    private String androidVersion;
    private String manufacturer;
    private String model;
}