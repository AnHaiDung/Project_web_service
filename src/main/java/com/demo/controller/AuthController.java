package com.demo.controller;

import com.demo.model.dto.request.ChangePasswordRequest;
import com.demo.model.dto.request.ForgotPasswordRequest;
import com.demo.model.dto.request.LoginRequest;
import com.demo.model.dto.request.RefreshTokenRequest;
import com.demo.model.dto.request.RegisterRequest;
import com.demo.model.dto.response.ApiResponse;
import com.demo.model.dto.response.AuthResponse;
import com.demo.model.dto.response.UserResponse;
import com.demo.model.entity.AccountRole;
import com.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Đăng nhập và trả về access token + refresh token.
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Đăng nhập thành công",
                authService.login(request),
                HttpStatus.OK.value()
        ));
    }

    // Cấp lại access token mới từ refresh token.
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cấp lại token truy cập thành công",
                authService.refresh(request),
                HttpStatus.OK.value()
        ));
    }

    // Đăng xuất bằng cách đưa access token hiện tại vào blacklist.
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(extractBearerToken(authorization));
        return ResponseEntity.ok(ApiResponse.success(
                "Đăng xuất thành công",
                null,
                HttpStatus.OK.value()
        ));
    }

    // Đổi mật khẩu cho tài khoản đang đăng nhập.
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Đổi mật khẩu thành công",
                null,
                HttpStatus.OK.value()
        ));
    }

    // Đặt lại mật khẩu đơn giản bằng username và email.
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Đặt lại mật khẩu thành công",
                null,
                HttpStatus.OK.value()
        ));
    }

    // Đăng ký tài khoản, role lấy từ body request.
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return created("Đăng ký tài khoản thành công", authService.register(request));
    }

    // Đăng ký tài khoản ứng viên, tự gán role CANDIDATE.
    @PostMapping("/register/candidate")
    public ResponseEntity<ApiResponse<UserResponse>> registerCandidate(@Valid @RequestBody RegisterRequest request) {
        request.setRole(AccountRole.CANDIDATE);
        return created("Đăng ký tài khoản ứng viên thành công", authService.register(request));
    }

    // Đăng ký tài khoản nhà tuyển dụng, tự gán role EMPLOYER.
    @PostMapping("/register/employer")
    public ResponseEntity<ApiResponse<UserResponse>> registerEmployer(@Valid @RequestBody RegisterRequest request) {
        request.setRole(AccountRole.EMPLOYER);
        return created("Đăng ký tài khoản nhà tuyển dụng thành công", authService.register(request));
    }

    // Tạo response 201 Created dùng chung cho các API đăng ký.
    private ResponseEntity<ApiResponse<UserResponse>> created(String message, UserResponse data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, data, HttpStatus.CREATED.value()));
    }

    // Cắt chuỗi "Bearer " để lấy JWT thật từ header Authorization.
    private String extractBearerToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
