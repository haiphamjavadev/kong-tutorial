package com.haiph.kongtutorial.controllers;

import com.haiph.kongtutorial.MinioService;
import com.haiph.kongtutorial.config.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/upload-file")
@RequiredArgsConstructor
public class UploadFileTestController {
    private final MinioService minioService;

    @PostMapping()
    public ResponseEntity<List<String>> uploadFileTest(@RequestPart List<MultipartFile> files, @RequestParam String tag) {
        String dateNow = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        List<UploadFile> lstPath = files.stream()
                .map(file -> UploadFile.builder()
                        .file(file)
                        .path(tag + dateNow + "/" + file.getOriginalFilename())
                        .build())
                .toList();
        List<String> urls = minioService.putLstWithAsync(lstPath);
        return ResponseEntity.ok(urls);
    }

    @PostMapping("/update/status")
    public ResponseEntity<List<String>> updateStatus(@RequestBody List<String> paths) {
        return ResponseEntity.ok(minioService.markAsPermanent(paths));
    }
}
