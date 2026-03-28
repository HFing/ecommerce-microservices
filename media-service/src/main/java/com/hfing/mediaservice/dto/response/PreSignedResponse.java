package com.hfing.mediaservice.dto.response;

import lombok.Builder;

@Builder
public record PreSignedResponse(
        String url,
        String key
) {
}
