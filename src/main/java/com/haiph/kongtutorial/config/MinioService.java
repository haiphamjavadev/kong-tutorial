package com.haiph.kongtutorial.config;

import com.haiph.kongtutorial.resp.FileUploadResponse;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.temp-link-expiry-hours}")
    private int tempLinkExpiryHours;

    @Value("${minio.permanent-link-expiry-days}")
    private int permanentLinkExpiryDays;

    /**
     * Upload file và tag là "temp"
     * Trả về presigned URL tạm thời (24h)
     */
    public FileUploadResponse uploadTempFile(MultipartFile file) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());

            // Upload file với tag "status=temp"
            Map<String, String> tags = new HashMap<>();
            tags.put("status", "temp");

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .tags(tags)
                            .build()
            );

            // Tạo presigned URL hết hạn sau 24h
            String tempUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(tempLinkExpiryHours, TimeUnit.HOURS)
                            .build()
            );

            log.info("File uploaded with temp tag: {}", fileName);

            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .tempUrl(tempUrl)
                    .expiryHours(tempLinkExpiryHours)
                    .build();

        } catch (Exception e) {
            log.error("Error uploading temp file", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Đổi tag từ "temp" sang "permanent"
     * File sẽ không bị xóa bởi lifecycle policy
     * Trả về presigned URL dài hạn
     */
    public String markAsPermanent(String fileName) {
        try {
            // Đổi tag thành "status=permanent"
            Map<String, String> tags = new HashMap<>();
            tags.put("status", "permanent");

            minioClient.setObjectTags(
                    SetObjectTagsArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .tags(tags)
                            .build()
            );

            // Tạo presigned URL dài hạn (7 ngày - max của MinIO)
            // Lưu ý: MinIO max expiry = 7 ngày
            int expiryDays = Math.min(permanentLinkExpiryDays, 7);
            String permanentUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(expiryDays, TimeUnit.DAYS)
                            .build()
            );

            log.info("File marked as permanent: {}", fileName);

            return permanentUrl;

        } catch (Exception e) {
            log.error("Error marking file as permanent", e);
            throw new RuntimeException("Failed to mark file as permanent", e);
        }
    }

    /**
     * Tạo presigned URL mới cho file permanent
     * Gọi khi URL cũ sắp hết hạn
     */
    public String refreshPermanentUrl(String fileName) {
        try {
            int expiryDays = Math.min(permanentLinkExpiryDays, 7);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(expiryDays, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error refreshing permanent URL", e);
            throw new RuntimeException("Failed to refresh URL", e);
        }
    }

    /**
     * Xóa file thủ công
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
            log.info("File deleted: {}", fileName);
        } catch (Exception e) {
            log.error("Error deleting file", e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Lấy thông tin tags của file
     */
    public Map<String, String> getFileTags(String fileName) {
        try {
            return minioClient.getObjectTags(
                    GetObjectTagsArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            ).get();
        } catch (Exception e) {
            log.error("Error getting file tags", e);
            return new HashMap<>();
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

}
