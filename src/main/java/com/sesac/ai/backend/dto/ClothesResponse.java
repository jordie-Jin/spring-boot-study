package com.sesac.ai.backend.dto;

import com.sesac.ai.backend.domain.Clothes;

public record ClothesResponse(
        Long id,
        String name,
        String category,
        String brand,
        String color,
        String size,
        Integer price,
        Integer stock,
        String material,
        String season,
        Boolean soldOut
) {
    public static ClothesResponse from(Clothes clothes) {
        return new ClothesResponse(
                clothes.getId(),
                clothes.getName(),
                clothes.getCategory(),
                clothes.getBrand(),
                clothes.getColor(),
                clothes.getSize(),
                clothes.getPrice(),
                clothes.getStock(),
                clothes.getMaterial(),
                clothes.getSeason(),
                clothes.getSoldOut()
        );
    }
}
