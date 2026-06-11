package com.demo.service.impl;

import com.demo.exception.ConflictException;
import com.demo.exception.NotFoundException;
import com.demo.model.dto.request.JobStatusRequest;
import com.demo.model.dto.request.UserCreateRequest;
import com.demo.model.dto.request.UserUpdateRequest;
import com.demo.model.dto.response.JobResponse;
import com.demo.model.dto.response.UserResponse;
import com.demo.model.entity.Job;
import com.demo.model.entity.JobStatus;
import com.demo.model.entity.User;
import com.demo.repository.JobRepository;
import com.demo.repository.UserRepository;
import com.demo.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    // Admin tìm kiếm danh sách người dùng theo username hoặc fullName.
    @Override
    public Page<UserResponse> getUsers(String keyword, Pageable pageable) {
        String search = keyword == null ? "" : keyword.trim();
        return userRepository.searchUsers(search, pageable)
                .map(this::toUserResponse);
    }

    // Admin tạo tài khoản mới và mã hóa mật khẩu.
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        validateNewUser(request.getUsername(), request.getEmail(), request.getPhone());
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .enabled(request.getEnabled() == null || request.getEnabled())
                .build();
        return toUserResponse(userRepository.save(user));
    }

    // Admin cập nhật thông tin tài khoản người dùng.
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUser(id);
        validateUpdateUser(id, request.getUsername(), request.getEmail(), request.getPhone());
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());
        return toUserResponse(user);
    }

    // Admin khóa tài khoản thay vì xóa hẳn khỏi database.
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        user.setEnabled(false);
    }

    // Admin xem danh sách tin, có thể tìm theo keyword hoặc lọc theo status.
    @Override
    public Page<JobResponse> getJobs(String keyword, JobStatus status, Pageable pageable) {
        if (status != null) {
            return jobRepository.findByStatus(status, pageable).map(this::toJobResponse);
        }
        String search = keyword == null ? "" : keyword.trim();
        return jobRepository.findByTitleContainingIgnoreCase(search, pageable).map(this::toJobResponse);
    }

    // Admin duyệt hoặc từ chối tin tuyển dụng.
    @Override
    @Transactional
    public JobResponse updateJobStatus(Long id, JobStatusRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tin tuyển dụng"));
        job.setStatus(request.getStatus());
        return toJobResponse(job);
    }

    // Tìm user theo id, không có thì ném lỗi 404.
    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }

    // Kiểm tra dữ liệu tạo user mới không bị trùng.
    private void validateNewUser(String username, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email đã tồn tại");
        }
        if (phone != null && !phone.isBlank() && userRepository.existsByPhone(phone)) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }
    }

    // Kiểm tra dữ liệu cập nhật user không trùng với user khác.
    private void validateUpdateUser(Long id, String username, String email, String phone) {
        if (userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new ConflictException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new ConflictException("Email đã tồn tại");
        }
        if (phone != null && !phone.isBlank() && userRepository.existsByPhoneAndIdNot(phone, id)) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }
    }

    // Chuyển entity User sang UserResponse để ẩn password.
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .cvUrl(user.getCvUrl())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }

    // Chuyển entity Job sang JobResponse trả về cho client.
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
