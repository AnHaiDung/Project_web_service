package com.demo.service.impl;

import com.demo.exception.ConflictException;
import com.demo.exception.NotFoundException;
import com.demo.model.dto.request.ApplicationRequest;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.JobResponse;
import com.demo.model.entity.Application;
import com.demo.model.entity.ApplicationStatus;
import com.demo.model.entity.Job;
import com.demo.model.entity.JobStatus;
import com.demo.model.entity.User;
import com.demo.repository.ApplicationRepository;
import com.demo.repository.JobRepository;
import com.demo.repository.UserRepository;
import com.demo.security.principal.CustomUserDetails;
import com.demo.service.CandidateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public Page<JobResponse> searchJobs(String keyword, Pageable pageable) {
        String search = keyword == null ? "" : keyword.trim();
        return jobRepository.findByStatusAndActiveTrueAndTitleContainingIgnoreCase(
                JobStatus.APPROVED,
                search,
                pageable
        ).map(this::toJobResponse);
    }

    @Override
    @Transactional
    public ApplicationResponse applyJob(ApplicationRequest request) {
        User candidate = getCurrentCandidate();
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tin tuyển dụng"));

        if (!Boolean.TRUE.equals(job.getActive()) || job.getStatus() != JobStatus.APPROVED) {
            throw new ConflictException("Tin tuyển dụng chưa được duyệt hoặc đã bị ẩn");
        }
        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now())) {
            throw new ConflictException("Tin tuyển dụng đã hết hạn ứng tuyển");
        }
        if (applicationRepository.existsByJobAndCandidate(job, candidate)) {
            throw new ConflictException("Bạn đã nộp hồ sơ vào tin tuyển dụng này");
        }

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .coverLetter(request.getCoverLetter())
                .cvUrl(request.getCvUrl())
                .status(ApplicationStatus.PENDING)
                .appliedAt(Instant.now())
                .build();

        return toApplicationResponse(applicationRepository.save(application));
    }

    @Override
    public Page<ApplicationResponse> getMyApplications(Pageable pageable) {
        User candidate = getCurrentCandidate();
        return applicationRepository.findByCandidate(candidate, pageable)
                .map(this::toApplicationResponse);
    }

    private User getCurrentCandidate() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy ứng viên"));
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

    private ApplicationResponse toApplicationResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .candidateId(application.getCandidate().getId())
                .candidateName(application.getCandidate().getFullName())
                .coverLetter(application.getCoverLetter())
                .cvUrl(application.getCvUrl())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
