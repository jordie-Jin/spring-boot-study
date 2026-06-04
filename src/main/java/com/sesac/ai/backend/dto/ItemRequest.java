package com.sesac.ai.backend.dto;

import com.sesac.ai.backend.domain.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemRequest(
        @NotBlank String name,
        @Min(0) int price
) {
    public Item toEntity() {
        return Item.builder().name(name).price(price).build();
    }
}
