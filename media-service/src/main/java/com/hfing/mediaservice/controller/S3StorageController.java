package com.hfing.mediaservice.controller;

import com.hfing.mediaservice.dto.response.ApiResponse;
import com.hfing.mediaservice.dto.response.FileResponse;
import com.hfing.mediaservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        var data = storageService.uploadFile(file);
        return ApiResponse.<FileResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("File uploaded successfully")
                .data(data)
                .build();
    }
}
