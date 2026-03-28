package com.hfing.mediaservice.service.impl;

import com.hfing.mediaservice.dto.response.FileResponse;
import com.hfing.mediaservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "S3-STORAGE")
public class S3StorageService implements StorageService {

    @Value("${aws.s3.bucket}")
    private String BUCKET_NAME;

    @Value("${aws.region.static}")
    private String REGION;

    private final S3Client s3Client;

    @Override
    public FileResponse uploadFile(MultipartFile file) throws IOException {
        // Get original filename and validate
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());

        // Generate unique file key with UUID and file extension
        String key = UUID.randomUUID() + "_" + originalFilename
                .substring(originalFilename.lastIndexOf("."));

        // Build PutObjectRequest with content type
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType(file.getContentType())
                .build();

        // Create RequestBody from file input stream
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        // Upload file to S3
        s3Client.putObject(putObjectRequest, requestBody);

        // Generate S3 URL
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, key);

        // Return file response
        return FileResponse.builder()
                .key(key)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .url(url)
                .build();
    }

    @Override
    public void deleteFile(String fileKey) {
    }

    @Override
    public String generatePresignedUrl(String fileKey) {
        return "";
    }
}