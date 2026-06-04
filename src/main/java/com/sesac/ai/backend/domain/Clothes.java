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
    private String name;
    private int price;
}
