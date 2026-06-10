package com.demo.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequest {
    @NotNull(message = "Mã tin tuyển dụng không được để trống")
    private Long jobId;

    private String coverLetter;

    private String cvUrl;
}
