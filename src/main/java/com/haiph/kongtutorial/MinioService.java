package com.haiph.kongtutorial;

import com.haiph.kongtutorial.config.UploadFile;
import org.springframework.http.HttpMethod;

import java.io.InputStream;
import java.util.List;

public interface MinioService {
    String getObjectPreSignedUrl(String objectKey, HttpMethod method);

    List<String> putLstWithAsync(List<UploadFile> lstPath);

    void removeObject(String objectKey);

    InputStream download(String objectFile);

    List<String> markAsPermanent(List<String> fileNames);
}
