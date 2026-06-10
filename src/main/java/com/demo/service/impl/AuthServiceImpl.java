package com.demo.service.impl;

import com.demo.exception.BadRequestException;
import com.demo.exception.ConflictException;
import com.demo.exception.ForbiddenException;
import com.demo.exception.InvalidTokenException;
import com.demo.model.dto.request.LoginRequest;
import com.demo.model.dto.request.RefreshTokenRequest;
import com.demo.model.dto.request.RegisterRequest;
import com.demo.model.dto.response.AuthResponse;
import com.demo.model.dto.response.UserResponse;
import com.demo.model.entity.AccountRole;
import com.demo.model.entity.RefreshToken;
import com.demo.model.entity.TokenBlacklist;
import com.demo.model.entity.User;
import com.demo.repository.RefreshTokenRepository;
import com.demo.repository.TokenBlacklistRepository;
import com.demo.repository.UserRepository;
import com.demo.security.jwt.JwtService;
import com.demo.security.principal.CustomUserDetails;
import com.demo.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt-refresh-expired}")
    private Long jwtRefreshExpired;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.getRole() == null) {
            throw new BadRequestException("Vai trò không được để trống");
        }
        if (request.getRole() == AccountRole.ADMIN) {
            throw new ForbiddenException("Đăng ký công khai chỉ hỗ trợ tài khoản ứng viên hoặc nhà tuyển dụng");
        }
        validateUniqueAccount(request);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .enabled(true)
                .build();

        return toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = createRefreshToken(userDetails.getUsername()).getToken();
            return toAuthResponse(userDetails, accessToken, refreshToken);
        } catch (DisabledException exception) {
            throw new ForbiddenException("Tài khoản đang bị khóa hoặc chưa được kích hoạt");
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Token làm mới không tồn tại"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new InvalidTokenException("Token làm mới đã bị thu hồi");
        }
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            throw new InvalidTokenException("Token làm mới đã hết hạn");
        }

        User user = refreshToken.getUser();
        CustomUserDetails userDetails = toUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        return toAuthResponse(userDetails, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new InvalidTokenException("Token truy cập không được để trống");
        }
        Instant expiresAt = jwtService.extractExpiration(accessToken);
        if (!tokenBlacklistRepository.existsByToken(accessToken)) {
            tokenBlacklistRepository.save(TokenBlacklist.builder()
                    .token(accessToken)
                    .expiresAt(expiresAt)
                    .build());
        }
    }

    private RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("Tài khoản không tồn tại"));
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(jwtRefreshExpired))
                .revoked(false)
                .user(user)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private void validateUniqueAccount(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }
    }

    private AuthResponse toAuthResponse(CustomUserDetails userDetails, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(userDetails.getId())
                .username(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }

    private CustomUserDetails toUserDetails(User user) {
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .authorities(java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .build();
    }
}
