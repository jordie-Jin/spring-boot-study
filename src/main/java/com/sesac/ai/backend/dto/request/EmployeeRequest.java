package com.sesac.ai.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
        @NotNull Long departmentId,
        @NotBlank String position,
        @NotBlank @Email String email
) {
}
