package com.sesac.ai.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank String name
) {
}
