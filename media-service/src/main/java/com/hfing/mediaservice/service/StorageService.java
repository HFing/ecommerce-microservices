package com.hfing.mediaservice.service;

import com.hfing.mediaservice.dto.response.FileResponse;
import com.hfing.mediaservice.dto.response.PreSignedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    FileResponse uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String fileKey);
    PreSignedResponse generatePresignedUrl(String fileName);
}