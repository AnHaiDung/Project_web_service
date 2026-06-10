package com.demo.model.dto.response;

import com.demo.model.entity.AccountRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private AccountRole role;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
