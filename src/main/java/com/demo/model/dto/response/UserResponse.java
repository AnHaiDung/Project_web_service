package com.demo.model.dto.response;

import com.demo.model.entity.AccountRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String cvUrl;
    private AccountRole role;
    private Boolean enabled;
}
