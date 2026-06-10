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
    Page<UserResponse> getUsers(String keyword, Pageable pageable);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    Page<JobResponse> getJobs(String keyword, JobStatus status, Pageable pageable);

    JobResponse updateJobStatus(Long id, JobStatusRequest request);
}
