package com.demo.model.dto.request;

import com.demo.model.entity.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStatusRequest {
    @NotNull(message = "Trạng thái tin tuyển dụng không được để trống")
    private JobStatus status;
}
