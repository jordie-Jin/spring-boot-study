package com.sesac.ai.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatLogRequest(
        @NotBlank String prompt,
        String response
) {
}
