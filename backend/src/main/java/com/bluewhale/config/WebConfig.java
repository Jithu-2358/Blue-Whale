// config/WebConfig.java
package com.bluewhale.config;

import com.bluewhale.storage.StorageProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StorageProperties props;

    public WebConfig(StorageProperties props) {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + props.getVideoPath() + "/");
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("file:" + props.getPhotoPath() + "/");
        registry.addResourceHandler("/apk-files/**")
                .addResourceLocations("file:" + props.getApkPath() + "/");
    }
}