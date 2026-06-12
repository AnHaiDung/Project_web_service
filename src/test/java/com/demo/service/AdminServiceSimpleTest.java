package com.demo.service;

import com.demo.model.dto.request.UserCreateRequest;
import com.demo.model.entity.AccountRole;
import com.demo.model.entity.User;
import com.demo.repository.JobRepository;
import com.demo.repository.UserRepository;
import com.demo.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceSimpleTest {
    // Test service tìm kiếm user có gọi đúng repository searchUsers.
    @Test
    void getUsers_shouldCallSearchUsers() {
        UserRepository userRepository = mock(UserRepository.class);
        JobRepository jobRepository = mock(JobRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AdminServiceImpl service = new AdminServiceImpl(userRepository, jobRepository, passwordEncoder);
        PageRequest pageable = PageRequest.of(0, 5);
        User user = User.builder()
                .id(1L)
                .username("admin")
                .fullName("Admin")
                .email("admin@gmail.com")
                .role(AccountRole.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.searchUsers("adm", pageable)).thenReturn(new PageImpl<>(List.of(user)));

        assertEquals(1, service.getUsers("adm", pageable).getTotalElements());
        verify(userRepository).searchUsers("adm", pageable);
    }

    // Test service tạo user có mã hóa mật khẩu trước khi lưu.
    @Test
    void createUser_shouldEncodePassword() {
        UserRepository userRepository = mock(UserRepository.class);
        JobRepository jobRepository = mock(JobRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AdminServiceImpl service = new AdminServiceImpl(userRepository, jobRepository, passwordEncoder);
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("employer01");
        request.setPassword("123456");
        request.setFullName("Cong ty ABC");
        request.setEmail("employer01@gmail.com");
        request.setPhone("0900000001");
        request.setRole(AccountRole.EMPLOYER);

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createUser(request);

        verify(passwordEncoder).encode("123456");
    }
}
