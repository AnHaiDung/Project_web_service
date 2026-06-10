package com.demo.controller;

import com.demo.model.dto.request.JobRequest;
import com.demo.model.dto.response.ApiResponse;
import com.demo.model.dto.response.JobResponse;
import com.demo.service.EmployerJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employer/jobs")
@RequiredArgsConstructor
public class EmployerJobController {
    private final EmployerJobService employerJobService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getMyJobs(@PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách tin tuyển dụng của tôi thành công",
                employerJobService.getMyJobs(pageable),
                HttpStatus.OK.value()
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Đăng tin tuyển dụng thành công, vui lòng chờ admin duyệt",
                employerJobService.createJob(request),
                HttpStatus.CREATED.value()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật tin tuyển dụng thành công, vui lòng chờ admin duyệt lại",
                employerJobService.updateJob(id, request),
                HttpStatus.OK.value()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        employerJobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Ẩn tin tuyển dụng thành công",
                null,
                HttpStatus.OK.value()
        ));
    }
}
