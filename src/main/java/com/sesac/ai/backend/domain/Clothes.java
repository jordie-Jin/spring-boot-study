package com.sesac.ai.backend.domain;

import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Clothes {

    private Long id;

    private String name;        // 상품명
    private String category;    // 상의, 하의, 아우터

    private String brand;       // 브랜드
    private String color;       // 색상
    private String size;        // M, L, XL

    private Integer price;      // 가격
    private Integer stock;      // 재고

    private String material;    // 소재
    private String season;      // 봄, 여름, 가을, 겨울

    private Boolean soldOut;    // 품절 여부
}
