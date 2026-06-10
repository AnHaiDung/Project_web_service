package com.demo.advice;

import com.demo.exception.BadRequestException;
import com.demo.exception.ConflictException;
import com.demo.exception.ForbiddenException;
import com.demo.exception.InvalidTokenException;
import com.demo.exception.NotFoundException;
import com.demo.model.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "Giá trị không hợp lệ" : error.getDefaultMessage(),
                        (first, second) -> first
                ));

        return error("Dữ liệu gửi lên không hợp lệ", errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException exception) {
        return error(exception.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException exception) {
        return error("Tên đăng nhập hoặc mật khẩu không đúng", null, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({InvalidTokenException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleInvalidToken(RuntimeException exception) {
        return error(exception.getMessage(), null, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(ForbiddenException exception) {
        return error(exception.getMessage(), null, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException exception) {
        return error(exception.getMessage(), null, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NotFoundException exception) {
        return error(exception.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiResponse<Object>> error(String message, Object errors, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, errors, status.value()));
    }
}
