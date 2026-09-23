// mapper/DeviceMapper.java
package com.bluewhale.mapper;

import com.bluewhale.dto.DeviceRegisterRequest;
import com.bluewhale.model.Device;
import java.time.Instant;

public class DeviceMapper {

    public static Device toEntity(DeviceRegisterRequest req, String ip) {
        Device d = new Device();
        d.setId(req.getDeviceId());
        d.setDeviceName(req.getDeviceName());
        d.setAndroidVersion(req.getAndroidVersion());
        d.setManufacturer(req.getManufacturer());
        d.setModel(req.getModel());
        d.setIpAddress(ip);
        d.setFirstSeen(Instant.now());
        d.setLastSeen(Instant.now());
        d.setActive(true);
        d.setMalwareActive(false);
        d.setStatus("dormant");
        return d;
    }

    public static void updateLastSeen(Device d) {
        d.setLastSeen(Instant.now());
    }
}