package com.demo.service;

import com.demo.model.dto.request.ApplicationRequest;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.CvUploadResponse;
import com.demo.model.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CandidateService {
    // Tìm kiếm tin tuyển dụng đã được duyệt.
    Page<JobResponse> searchJobs(String keyword, Pageable pageable);

    // Nộp hồ sơ ứng tuyển.
    ApplicationResponse applyJob(ApplicationRequest request);

    // Xem hồ sơ đã nộp.
    Page<ApplicationResponse> getMyApplications(Pageable pageable);

    // Tải lên CV PDF.
    CvUploadResponse uploadCv(MultipartFile file);
}
