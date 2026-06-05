package com.sesac.ai.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank String name
) {
}
