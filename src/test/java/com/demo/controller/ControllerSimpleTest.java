package com.demo.controller;

import com.demo.model.dto.request.ApplicationRequest;
import com.demo.model.dto.request.ChangePasswordRequest;
import com.demo.model.dto.request.JobRequest;
import com.demo.model.dto.request.LoginRequest;
import com.demo.model.dto.request.RefreshTokenRequest;
import com.demo.model.dto.request.UserCreateRequest;
import com.demo.model.dto.response.ApplicationResponse;
import com.demo.model.dto.response.AuthResponse;
import com.demo.model.dto.response.CvUploadResponse;
import com.demo.model.dto.response.JobResponse;
import com.demo.model.dto.response.UserResponse;
import com.demo.service.AdminService;
import com.demo.service.AuthService;
import com.demo.service.CandidateService;
import com.demo.service.EmployerJobService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerSimpleTest {
    // Test controller đăng nhập trả về 200 OK.
    @Test
    void login_shouldReturnOk() {
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        when(authService.login(request)).thenReturn(AuthResponse.builder().username("admin").build());

        assertEquals(HttpStatus.OK, controller.login(request).getStatusCode());
    }

    // Test controller làm mới token trả về 200 OK.
    @Test
    void refresh_shouldReturnOk() {
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(authService.refresh(request)).thenReturn(AuthResponse.builder().accessToken("new-token").build());

        assertEquals(HttpStatus.OK, controller.refresh(request).getStatusCode());
    }

    // Test controller đổi mật khẩu trả về 200 OK.
    @Test
    void changePassword_shouldReturnOk() {
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("123456789");

        assertEquals(HttpStatus.OK, controller.changePassword(request).getStatusCode());
    }

    // Test controller admin lấy danh sách user trả về 200 OK.
    @Test
    void getUsers_shouldReturnOk() {
        AdminService adminService = mock(AdminService.class);
        AdminController controller = new AdminController(adminService);
        PageRequest pageable = PageRequest.of(0, 5);

        when(adminService.getUsers("", pageable)).thenReturn(new PageImpl<>(List.of(UserResponse.builder().id(1L).build())));

        assertEquals(HttpStatus.OK, controller.getUsers("", pageable).getStatusCode());
    }

    // Test controller admin tạo user trả về 201 Created.
    @Test
    void createUser_shouldReturnCreated() {
        AdminService adminService = mock(AdminService.class);
        AdminController controller = new AdminController(adminService);
        UserCreateRequest request = new UserCreateRequest();

        when(adminService.createUser(request)).thenReturn(UserResponse.builder().id(1L).build());

        assertEquals(HttpStatus.CREATED, controller.createUser(request).getStatusCode());
    }

    // Test controller employer đăng tin tuyển dụng trả về 201 Created.
    @Test
    void createJob_shouldReturnCreated() {
        EmployerJobService employerJobService = mock(EmployerJobService.class);
        EmployerJobController controller = new EmployerJobController(employerJobService);
        JobRequest request = new JobRequest();

        when(employerJobService.createJob(request)).thenReturn(JobResponse.builder().id(1L).build());

        assertEquals(HttpStatus.CREATED, controller.createJob(request).getStatusCode());
    }

    // Test controller candidate nộp hồ sơ trả về 201 Created.
    @Test
    void applyJob_shouldReturnCreated() {
        CandidateService candidateService = mock(CandidateService.class);
        CandidateController controller = new CandidateController(candidateService);
        ApplicationRequest request = new ApplicationRequest();
        request.setJobId(1L);

        when(candidateService.applyJob(request)).thenReturn(ApplicationResponse.builder().id(1L).build());

        assertEquals(HttpStatus.CREATED, controller.applyJob(request).getStatusCode());
    }

    // Test controller candidate upload CV trả về 200 OK.
    @Test
    void uploadCv_shouldReturnOk() {
        CandidateService candidateService = mock(CandidateService.class);
        CandidateController controller = new CandidateController(candidateService);
        MultipartFile file = mock(MultipartFile.class);

        when(candidateService.uploadCv(file)).thenReturn(CvUploadResponse.builder().cvUrl("/uploads/cv/demo.pdf").build());

        assertEquals(HttpStatus.OK, controller.uploadCv(file).getStatusCode());
    }
}
