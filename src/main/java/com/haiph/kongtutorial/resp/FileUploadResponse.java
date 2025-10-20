package com.haiph.kongtutorial.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    private String fileName;
    private String tempUrl;
    private int expiryHours;
}