package com.sesac.ai.backend.dto;

import com.sesac.ai.backend.domain.Clothes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClothesRequest(
        @NotBlank String name,
        @NotBlank String category,
        String brand,
        String color,
        String size,
        @NotNull @Min(0) Integer price,
        @NotNull @Min(0) Integer stock,
        String material,
        String season,
        Boolean soldOut
) {
    public Clothes toEntity() {
        return Clothes.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .color(color)
                .size(size)
                .price(price)
                .stock(stock)
                .material(material)
                .season(season)
                .soldOut(soldOut)
                .build();
    }
}
