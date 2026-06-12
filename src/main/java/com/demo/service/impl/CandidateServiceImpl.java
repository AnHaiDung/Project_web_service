package com.demo.service.impl;

import com.demo.exception.ConflictException;
import com.demo.exception.BadRequestException;
import com.demo.exception.NotFoundException;
import com.demo.model.dto.request.ApplicationRequest;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.CvUploadResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {
    private static final long MAX_CV_SIZE = 15 * 1024 * 1024;

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    // Ứng viên tìm các tin đã duyệt, đang active và có tiêu đề chứa keyword.
    @Override
    public Page<JobResponse> searchJobs(String keyword, Pageable pageable) {
        String search = keyword == null ? "" : keyword.trim();
        return jobRepository.findByStatusAndActiveTrueAndTitleContainingIgnoreCase(
                JobStatus.APPROVED,
                search,
                pageable
        ).map(this::toJobResponse);
    }

    // Ứng viên nộp hồ sơ vào tin tuyển dụng hợp lệ.
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
        if (candidate.getCvUrl() == null || candidate.getCvUrl().isBlank()) {
            throw new BadRequestException("Vui lòng upload CV trước khi nộp hồ sơ");
        }

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .coverLetter(request.getCoverLetter())
                .cvUrl(candidate.getCvUrl())
                .status(ApplicationStatus.PENDING)
                .appliedAt(Instant.now())
                .build();

        return toApplicationResponse(applicationRepository.save(application));
    }

    // Lấy danh sách hồ sơ mà ứng viên hiện tại đã nộp.
    @Override
    public Page<ApplicationResponse> getMyApplications(Pageable pageable) {
        User candidate = getCurrentCandidate();
        return applicationRepository.findByCandidate(candidate, pageable)
                .map(this::toApplicationResponse);
    }

    // Upload CV PDF vào thư mục local và lưu đường dẫn vào tài khoản ứng viên.
    @Override
    @Transactional
    public CvUploadResponse uploadCv(MultipartFile file) {
        User candidate = getCurrentCandidate();
        validatePdf(file);

        String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "cv.pdf");
        String safeName = originalName.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
        String fileName = "candidate_" + candidate.getId() + "_" + System.currentTimeMillis() + "_" + safeName;
        Path uploadDir = Path.of("uploads", "cv").toAbsolutePath().normalize();
        Path target = uploadDir.resolve(fileName).normalize();

        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new BadRequestException("Không thể lưu file CV");
        }

        String cvUrl = "/uploads/cv/" + fileName;
        candidate.setCvUrl(cvUrl);

        return CvUploadResponse.builder()
                .candidateId(candidate.getId())
                .fileName(fileName)
                .cvUrl(cvUrl)
                .build();
    }

    // Kiểm tra file CV: không rỗng, không quá 15MB và phải là PDF.
    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File CV không được để trống");
        }
        if (file.getSize() > MAX_CV_SIZE) {
            throw new BadRequestException("File CV không được vượt quá 15MB");
        }

        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename();
        boolean isPdfContent = "application/pdf".equalsIgnoreCase(contentType);
        boolean isPdfName = originalName != null && originalName.toLowerCase().endsWith(".pdf");
        if (!isPdfContent && !isPdfName) {
            throw new BadRequestException("Chỉ cho phép upload file PDF");
        }
    }

    // Lấy ứng viên đang đăng nhập từ SecurityContext.
    private User getCurrentCandidate() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy ứng viên"));
    }

    // Chuyển entity Job sang JobResponse.
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

    // Chuyển entity Application sang ApplicationResponse.
    private ApplicationResponse toApplicationResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .candidateId(application.getCandidate().getId())
                .candidateName(application.getCandidate().getFullName())
                .coverLetter(application.getCoverLetter())
                .cvUrl(application.getCvUrl())
                .feedback(application.getFeedback())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
