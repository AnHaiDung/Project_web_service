package com.demo.model.dto.response;

import com.demo.model.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String coverLetter;
    private String cvUrl;
    private String feedback;
    private ApplicationStatus status;
    private Instant appliedAt;
}
