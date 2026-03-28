package com.hfing.mediaservice.service.impl;

import com.hfing.mediaservice.dto.response.FileResponse;
import com.hfing.mediaservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "S3-STORAGE")
public class S3StorageService implements StorageService {

    @Override
    public FileResponse uploadFile(MultipartFile file) throws IOException {
        return null;
    }

    @Override
    public void deleteFile(String fileKey) {
    }

    @Override
    public String generatePresignedUrl(String fileKey) {
        return "";
    }
}
