package com.haiph.kongtutorial.config;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetObjectTagsArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(MinioProperties.class)
public class MinioServiceImpl implements com.haiph.kongtutorial.MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperty;

    @Override
    public String getObjectPreSignedUrl(String objectKey, HttpMethod method) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(getMethodFromName(method))
                    .bucket(minioProperty.getBucket())
                    .object(objectKey)
                    .expiry(minioProperty.getPreSignedUrlExpiration(), TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            log.error("getObjectPreSignedUrl() - Error while getting preSigned url {} {}", minioProperty.getBucket(), objectKey, e);
//            throw new ServiceExchangeException(e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> putLstWithAsync(List<UploadFile> lstPath) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        try {
            for (UploadFile file : lstPath) {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try (InputStream stream = file.getFile().getInputStream()) {
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(minioProperty.getBucket())
                                        .object(file.getPath())
                                        .tags(Map.of("status", "temp"))
                                        .stream(stream, -1, 10 * 1024 * 1024)
                                        .build()
                        );
                        log.info("Uploaded {}", file.getPath());
                        return file.getPath(); // ✅ Trả về path đã upload
                    } catch (Exception e) {
                        log.error("Upload failed for {}: {}", file.getPath(), e.getMessage(), e);
                        return null; // hoặc throw nếu muốn fail toàn bộ
                    }
                }, executor);
                futures.add(future);
            }

            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("putLstWithAsync() - Error while uploading list of files", e);
//            throw new ServiceExchangeException(e.getMessage());
            return null;
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public void removeObject(String objectKey) {
        try {
            if (isBucketNotExits(minioProperty.getBucket())) {
//                throw new ServiceExchangeException(String.format(CommonErrorCode.NOT_FOUND_MINIO_BUCKET.getKey(), minioProperty.getBucket()));
            }

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperty.getBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String objectFile) {
        InputStream inputStream;
        try {
            if (isBucketNotExits(minioProperty.getBucket())) {
//                throw new ServiceExchangeException(String.format(CommonErrorCode.NOT_FOUND_MINIO_BUCKET.getKey(), minioProperty.getBucket()));
            }
            inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperty.getBucket())
                            .object(objectFile)
                            .build());

            return inputStream;
        } catch (Exception e) {
            log.error("Error while get object", e);
//            throw new ServiceExchangeException(String.format(CommonErrorCode.INTERNAL_MINIO_PUT_OBJECT.getKey(), minioProperty.getBucket(), objectFile));
            return null;
        }

    }

    @Override
    public List<String> markAsPermanent(List<String> fileNames) {
        Map<String, String> tags = Map.of("status", "permanent");
        return fileNames.stream().map(fileName -> {
                    try {
                        minioClient.setObjectTags(
                                SetObjectTagsArgs.builder()
                                        .bucket(minioProperty.getBucket())
                                        .object(fileName)
                                        .tags(tags)
                                        .build()
                        );

                        log.info("Marked {} as permanent", fileName);
                        return fileName;

                    } catch (Exception e) {
                        log.error("Error marking {} as permanent", fileName, e);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .toList();
    }

    private boolean isBucketNotExits(String bucketName) {
        try {
            return !minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("isBucketExists() - Error while checking bucket exists", e);
            return true;
        }
    }

    private Method getMethodFromName(HttpMethod httpMethod) {
        try {
            return switch (httpMethod.name()) {
                case "GET" -> Method.GET;
                case "PUT" -> Method.PUT;
                case "POST" -> Method.POST;
                case "DELETE" -> Method.DELETE;
                case "HEAD" -> Method.HEAD;
                default -> throw new IllegalStateException("Unexpected value: " + httpMethod.name());
            };
        } catch (Exception e) {
            log.error("Error while getting method from name {}", e.getMessage(), e);
            return Method.GET;
        }
    }

}
