package com.haiph.kongtutorial.config;

import io.minio.*;
import io.minio.messages.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.Collections;

@Slf4j
@Configuration
public class MinioConfiguration {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket.temp}")
    private String tempBucket;

    @Value("${minio.bucket.permanent}")
    private String permanentBucket;

    @Value("${minio.temp-expiration-days}")
    private int tempExpirationDays;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @PostConstruct
    public void init() {
        try {
            MinioClient client = minioClient();

            createBucketIfNotExists(client, tempBucket);
            createBucketIfNotExists(client, permanentBucket);

            setupTempBucketLifecycle(client);

            log.info("✅ MinIO initialized successfully");
        } catch (Exception e) {
            log.error("❌ MinIO init error", e);
        }
    }

    private void createBucketIfNotExists(MinioClient client, String bucketName) throws Exception {
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("🪣 Bucket '{}' created", bucketName);
        }
    }

    private void setupTempBucketLifecycle(MinioClient client) throws Exception {
        RuleFilter filter = new RuleFilter(""); // áp dụng toàn bucket
        LifecycleRule rule = getLifecycleRule(filter);

        LifecycleConfiguration config = new LifecycleConfiguration(Collections.singletonList(rule));

        client.setBucketLifecycle(
                SetBucketLifecycleArgs.builder()
                        .bucket(tempBucket)
                        .config(config)
                        .build()
        );

        log.info("Lifecycle set for '{}' (auto-delete after {} days)", tempBucket, tempExpirationDays);
    }

    @NotNull
    private LifecycleRule getLifecycleRule(RuleFilter filter) {
        ZonedDateTime expireAt = ZonedDateTime.now().plusMinutes(5);
        Expiration expiration = new Expiration(expireAt, tempExpirationDays, null);
        AbortIncompleteMultipartUpload abort = new AbortIncompleteMultipartUpload(1);

        return new LifecycleRule(
                Status.ENABLED,                // trạng thái
                abort,                         // abort multipart
                expiration,                    // hết hạn
                filter,                        // filter rule
                "auto-delete-temp-files",      // id rule
                null,                          // noncurrent version expiration
                null,                          // noncurrent version transition
                null                           // transition
        );
    }
}
