package com.demo.service.impl;

import com.demo.exception.ForbiddenException;
import com.demo.exception.NotFoundException;
import com.demo.model.dto.request.JobRequest;
import com.demo.model.dto.response.JobResponse;
import com.demo.model.entity.Job;
import com.demo.model.entity.JobStatus;
import com.demo.model.entity.User;
import com.demo.repository.JobRepository;
import com.demo.repository.UserRepository;
import com.demo.security.principal.CustomUserDetails;
import com.demo.service.EmployerJobService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmployerJobServiceImpl implements EmployerJobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public Page<JobResponse> getMyJobs(Pageable pageable) {
        User employer = getCurrentEmployer();
        return jobRepository.findByEmployer(employer, pageable).map(this::toJobResponse);
    }

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request) {
        User employer = getCurrentEmployer();
        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .salary(request.getSalary())
                .deadline(request.getDeadline())
                .status(JobStatus.PENDING)
                .active(true)
                .createdAt(Instant.now())
                .employer(employer)
                .build();
        return toJobResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = findOwnJob(id);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setDeadline(request.getDeadline());
        job.setStatus(JobStatus.PENDING);
        return toJobResponse(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        Job job = findOwnJob(id);
        job.setActive(false);
    }

    private Job findOwnJob(Long id) {
        User employer = getCurrentEmployer();
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tin tuyển dụng"));
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new ForbiddenException("Bạn không có quyền thao tác với tin tuyển dụng này");
        }
        return job;
    }

    private User getCurrentEmployer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    }

    private JobResponse toJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salary(job.getSalary())
                .deadline(job.getDeadline())
                .status(job.getStatus())
                .active(job.getActive())
                .createdAt(job.getCreatedAt())
                .employerId(job.getEmployer().getId())
                .employerName(job.getEmployer().getFullName())
                .build();
    }
}
