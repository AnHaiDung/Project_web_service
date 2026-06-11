package com.demo.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CvUploadResponse {
    private Long candidateId;
    private String fileName;
    private String cvUrl;
}
