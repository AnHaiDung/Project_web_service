package com.demo.model.dto.response;

import com.demo.model.entity.JobStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String salary;
    private LocalDate deadline;
    private JobStatus status;
    private Boolean active;
    private Instant createdAt;
    private Long employerId;
    private String employerName;
}
