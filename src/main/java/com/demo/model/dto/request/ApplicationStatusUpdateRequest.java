package com.demo.model.dto.request;

import com.demo.model.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusUpdateRequest {
    @NotNull(message = "Trạng thái hồ sơ không được để trống")
    private ApplicationStatus status;

    private String feedback;
}
