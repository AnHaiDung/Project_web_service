package com.demo.service;

import com.demo.model.dto.request.JobStatusRequest;
import com.demo.model.dto.request.UserCreateRequest;
import com.demo.model.dto.request.UserUpdateRequest;
import com.demo.model.dto.response.JobResponse;
import com.demo.model.dto.response.UserResponse;
import com.demo.model.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    // Tìm kiếm và phân trang người dùng.
    Page<UserResponse> getUsers(String keyword, Pageable pageable);

    // Tạo tài khoản người dùng mới.
    UserResponse createUser(UserCreateRequest request);

    // Cập nhật tài khoản người dùng.
    UserResponse updateUser(Long id, UserUpdateRequest request);

    // Khóa tài khoản người dùng.
    void deleteUser(Long id);

    // Tìm kiếm, lọc và phân trang tin tuyển dụng.
    Page<JobResponse> getJobs(String keyword, JobStatus status, Pageable pageable);

    // Duyệt hoặc từ chối tin tuyển dụng.
    JobResponse updateJobStatus(Long id, JobStatusRequest request);
}
