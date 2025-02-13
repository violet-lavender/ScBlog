package com.wyz.resource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OBS配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "obs")
public class OBSConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;

}
