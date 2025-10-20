package com.haiph.kongtutorial.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketLifecycleArgs;
import io.minio.messages.AndOperator;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioSetupService {

    private final MinioClient minioClient;
    private final MinioProperties minioClientConfiguration;

    @PostConstruct
    public void init() {
        try {
            MinioClient client = minioClient;
            createBucketIfNotExists(client, minioClientConfiguration.getBucket());
            setupLifecyclePolicy(client);
            log.info("MinIO initialized successfully");
        } catch (Exception e) {
            log.error("MinIO init error", e);
        }
    }

    private void createBucketIfNotExists(MinioClient client, String bucketName) throws Exception {
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket {} created", bucketName);
        }
    }

    private void setupLifecyclePolicy(MinioClient client) throws Exception {
        Map<String, String> tags = new HashMap<>();
        tags.put("status", "temp");

        AndOperator andOperator = new AndOperator("", tags);

        LifecycleRule rule = this.getLifecycleRule(andOperator);

        LifecycleConfiguration config = new LifecycleConfiguration(Collections.singletonList(rule));

        client.setBucketLifecycle(
                SetBucketLifecycleArgs.builder()
                        .bucket(minioClientConfiguration.getBucket())
                        .config(config)
                        .build()
        );

        log.info("Lifecycle policy set for '{}': delete files tagged 'status=temp' after 1 day", minioClientConfiguration.getBucket());
    }

    @NotNull
    private LifecycleRule getLifecycleRule(AndOperator andOperator) {
        RuleFilter filter = new RuleFilter(andOperator);

        ZonedDateTime expTime = null;
        Expiration expiration = new Expiration(expTime, minioClientConfiguration.getTempExpirationMinutes(), null);

        return new LifecycleRule(
                Status.ENABLED,                // bật rule
                null,                          // abortIncompleteMultipartUpload
                expiration,                    // hết hạn
                filter,                        // filter rule
                "delete-temp-files",           // rule ID
                null,                          // noncurrentVersionExpiration
                null,                          // noncurrentVersionTransition
                null                           // transition
        );
    }

}
