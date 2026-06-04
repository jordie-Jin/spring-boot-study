package com.sesac.ai.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClothesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndGetClothes() throws Exception {
        String body = """
                {
                  "name": "Oxford Shirt",
                  "category": "top",
                  "brand": "Sesac",
                  "color": "white",
                  "size": "M",
                  "price": 39000,
                  "stock": 12,
                  "material": "cotton",
                  "season": "spring",
                  "soldOut": false
                }
                """;

        mockMvc.perform(post("/clothes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/clothes/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Oxford Shirt")))
                .andExpect(jsonPath("$.category", is("top")))
                .andExpect(jsonPath("$.price", is(39000)))
                .andExpect(jsonPath("$.stock", is(12)))
                .andExpect(jsonPath("$.soldOut", is(false)));

        mockMvc.perform(get("/clothes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Oxford Shirt")));
    }
}
