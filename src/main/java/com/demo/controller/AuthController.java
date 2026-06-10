package com.demo.controller;

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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Đăng nhập thành công",
                authService.login(request),
                HttpStatus.OK.value()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cấp lại token truy cập thành công",
                authService.refresh(request),
                HttpStatus.OK.value()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(extractBearerToken(authorization));
        return ResponseEntity.ok(ApiResponse.success(
                "Đăng xuất thành công",
                null,
                HttpStatus.OK.value()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return created("Đăng ký tài khoản thành công", authService.register(request));
    }

    @PostMapping("/register/candidate")
    public ResponseEntity<ApiResponse<UserResponse>> registerCandidate(@Valid @RequestBody RegisterRequest request) {
        request.setRole(AccountRole.CANDIDATE);
        return created("Đăng ký tài khoản ứng viên thành công", authService.register(request));
    }

    @PostMapping("/register/employer")
    public ResponseEntity<ApiResponse<UserResponse>> registerEmployer(@Valid @RequestBody RegisterRequest request) {
        request.setRole(AccountRole.EMPLOYER);
        return created("Đăng ký tài khoản nhà tuyển dụng thành công", authService.register(request));
    }

    private ResponseEntity<ApiResponse<UserResponse>> created(String message, UserResponse data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, data, HttpStatus.CREATED.value()));
    }

    private String extractBearerToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
