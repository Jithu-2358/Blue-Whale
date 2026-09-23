// storage/StorageProperties.java
package com.bluewhale.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {
    private String videoPath = "./videos";
    private String photoPath = "./photos";
    private String apkPath = "./apk";
}