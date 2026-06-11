package com.demo.service;

import com.demo.model.dto.request.ChangePasswordRequest;
import com.demo.model.dto.request.ForgotPasswordRequest;
import com.demo.model.dto.request.LoginRequest;
import com.demo.model.dto.request.RefreshTokenRequest;
import com.demo.model.dto.request.RegisterRequest;
import com.demo.model.dto.response.AuthResponse;
import com.demo.model.dto.response.UserResponse;

public interface AuthService {
    // Đăng ký tài khoản mới.
    UserResponse register(RegisterRequest request);

    // Đăng nhập và trả về token.
    AuthResponse login(LoginRequest request);

    // Làm mới access token.
    AuthResponse refresh(RefreshTokenRequest request);

    // Đăng xuất và thu hồi access token.
    void logout(String accessToken);

    // Đổi mật khẩu khi đã đăng nhập.
    void changePassword(ChangePasswordRequest request);

    // Đặt lại mật khẩu bằng username và email.
    void forgotPassword(ForgotPasswordRequest request);
}
