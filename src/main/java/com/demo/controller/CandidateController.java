package com.demo.controller;

import com.demo.model.dto.request.ApplicationRequest;
import com.demo.model.dto.response.ApiResponse;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.CvUploadResponse;
import com.demo.model.dto.response.JobResponse;
import com.demo.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateService candidateService;

    // Ứng viên tìm kiếm các tin tuyển dụng đã được duyệt.
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> searchJobs(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tìm kiếm tin tuyển dụng thành công",
                candidateService.searchJobs(keyword, pageable),
                HttpStatus.OK.value()
        ));
    }

    // Ứng viên nộp hồ sơ vào một tin tuyển dụng.
    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyJob(
            @Valid @RequestBody ApplicationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Nộp hồ sơ ứng tuyển thành công",
                candidateService.applyJob(request),
                HttpStatus.CREATED.value()
        ));
    }

    // Ứng viên xem danh sách hồ sơ mình đã nộp.
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getMyApplications(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách hồ sơ đã nộp thành công",
                candidateService.getMyApplications(pageable),
                HttpStatus.OK.value()
        ));
    }

    // Ứng viên tải lên file CV định dạng PDF.
    @PostMapping("/cv/upload")
    public ResponseEntity<ApiResponse<CvUploadResponse>> uploadCv(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tải lên CV thành công",
                candidateService.uploadCv(file),
                HttpStatus.OK.value()
        ));
    }
}
