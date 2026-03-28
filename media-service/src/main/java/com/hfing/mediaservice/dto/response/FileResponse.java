package com.hfing.mediaservice.dto.response;

import lombok.Builder;

@Builder
public record FileResponse(
        String key,
        String fileName,
        String contentType,
        long size,
        String url
) {
}
