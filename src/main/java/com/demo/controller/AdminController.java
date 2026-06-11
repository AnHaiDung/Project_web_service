package com.demo.controller;

import com.demo.model.dto.request.JobStatusRequest;
import com.demo.model.dto.request.UserCreateRequest;
import com.demo.model.dto.request.UserUpdateRequest;
import com.demo.model.dto.response.ApiResponse;
import com.demo.model.dto.response.JobResponse;
import com.demo.model.dto.response.UserResponse;
import com.demo.model.entity.JobStatus;
import com.demo.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // Admin xem và tìm kiếm danh sách người dùng, có phân trang.
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách người dùng thành công",
                adminService.getUsers(keyword, pageable),
                HttpStatus.OK.value()
        ));
    }

    // Admin tạo mới người dùng với role được gửi từ request.
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Tạo người dùng thành công",
                adminService.createUser(request),
                HttpStatus.CREATED.value()
        ));
    }

    // Admin cập nhật thông tin người dùng theo id.
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật người dùng thành công",
                adminService.updateUser(id, request),
                HttpStatus.OK.value()
        ));
    }

    // Admin khóa tài khoản người dùng bằng cách set enabled = false.
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Khóa người dùng thành công",
                null,
                HttpStatus.OK.value()
        ));
    }

    // Admin xem, tìm kiếm hoặc lọc tin tuyển dụng theo trạng thái.
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getJobs(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) JobStatus status,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách tin tuyển dụng thành công",
                adminService.getJobs(keyword, status, pageable),
                HttpStatus.OK.value()
        ));
    }

    // Admin duyệt hoặc từ chối tin tuyển dụng.
    @PatchMapping("/jobs/{id}/status")
    public ResponseEntity<ApiResponse<JobResponse>> updateJobStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật trạng thái tin tuyển dụng thành công",
                adminService.updateJobStatus(id, request),
                HttpStatus.OK.value()
        ));
    }
}
