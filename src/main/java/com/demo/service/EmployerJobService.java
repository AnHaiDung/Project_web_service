package com.demo.service;

import com.demo.model.dto.request.JobRequest;
import com.demo.model.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployerJobService {
    Page<JobResponse> getMyJobs(Pageable pageable);

    JobResponse createJob(JobRequest request);

    JobResponse updateJob(Long id, JobRequest request);

    void deleteJob(Long id);
}
