package com.demo.service;

import com.demo.model.dto.request.ApplicationRequest;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CandidateService {
    Page<JobResponse> searchJobs(String keyword, Pageable pageable);

    ApplicationResponse applyJob(ApplicationRequest request);

    Page<ApplicationResponse> getMyApplications(Pageable pageable);
}
