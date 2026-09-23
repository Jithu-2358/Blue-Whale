// exception/DeviceNotFoundException.java
package com.bluewhale.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String deviceId) {
        super("Device not found: " + deviceId);
    }
}