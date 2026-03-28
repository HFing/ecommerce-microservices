package com.hfing.mediaservice.service.impl;

import com.hfing.mediaservice.dto.response.FileResponse;
import com.hfing.mediaservice.dto.response.PreSignedResponse;
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
import java.util.UUID;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "S3-STORAGE")
public class S3StorageService implements StorageService {

    @Value("${aws.s3.bucket}")
    private String BUCKET_NAME;

    @Value("${aws.region.static}")
    private String REGION;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public FileResponse uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        String key = generateKey(originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType(file.getContentType())
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        s3Client.putObject(putObjectRequest, requestBody);

        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, key);

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
    public PreSignedResponse generatePresignedUrl(String fileName) {
        // Generate unique key
        String key = generateKey(fileName);

        // Build PutObjectRequest
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        // Build PutObjectPresignRequest with expiration time
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        // Generate presigned URL
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String url = presignedRequest.url().toExternalForm();
        return PreSignedResponse.builder()
                .url(url)
                .key(key)
                .build();
    }

    private String generateKey(String originalFilename) {
        String uuid = UUID.randomUUID().toString();

        return originalFilename != null
                ? uuid + "_" + originalFilename.substring(0, originalFilename.lastIndexOf("."))
                : uuid;
    }
}
