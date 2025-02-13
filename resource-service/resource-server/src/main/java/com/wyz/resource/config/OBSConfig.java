package com.wyz.resource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "obs")
public class OBSConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;

}
