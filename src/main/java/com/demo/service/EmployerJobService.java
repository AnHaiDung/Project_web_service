package com.demo.service;

import com.demo.model.dto.request.ApplicationStatusUpdateRequest;
import com.demo.model.dto.request.JobRequest;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployerJobService {
    // Lấy tin tuyển dụng của nhà tuyển dụng hiện tại.
    Page<JobResponse> getMyJobs(Pageable pageable);

    // Đăng tin tuyển dụng mới.
    JobResponse createJob(JobRequest request);

    // Cập nhật tin tuyển dụng.
    JobResponse updateJob(Long id, JobRequest request);

    // Ẩn tin tuyển dụng.
    void deleteJob(Long id);

    // Xem hồ sơ ứng tuyển vào các tin của mình.
    Page<ApplicationResponse> getApplications(Pageable pageable);

    // Cập nhật trạng thái hồ sơ ứng tuyển.
    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request);
}
