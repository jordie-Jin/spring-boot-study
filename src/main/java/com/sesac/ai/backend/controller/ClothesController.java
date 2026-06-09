package com.sesac.ai.backend.controller;

import com.sesac.ai.backend.dto.request.ClothesRequest;
import com.sesac.ai.backend.dto.response.ClothesResponse;
import com.sesac.ai.backend.service.ClothesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clothes")
@RequiredArgsConstructor
public class ClothesController {

    private final ClothesService clothesService;

    @GetMapping
    public List<ClothesResponse> list() {
        return clothesService.list();
    }

    @GetMapping("/{id}")
    public ClothesResponse get(@PathVariable Long id) {
        return clothesService.get(id);
    }

    @PostMapping
    public ResponseEntity<ClothesResponse> create(@Valid @RequestBody ClothesRequest req) {
        ClothesResponse created = clothesService.create(req);
        return ResponseEntity.created(URI.create("/clothes/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ClothesResponse update(@PathVariable Long id, @Valid @RequestBody ClothesRequest req) {
        return clothesService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clothesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
