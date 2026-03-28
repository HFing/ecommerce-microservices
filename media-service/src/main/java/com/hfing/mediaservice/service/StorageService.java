package com.hfing.mediaservice.service;

import com.hfing.mediaservice.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    FileResponse uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String fileKey);
    String generatePresignedUrl(String fileKey);
}