package com.haiph.kongtutorial.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "minio")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@Setter
public class MinioProperties {
    String endpoint;
    String accessKey;
    String secretKey;
    String bucket;
    int tempExpirationMinutes = 1;
    int preSignedUrlExpiration = 500;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
