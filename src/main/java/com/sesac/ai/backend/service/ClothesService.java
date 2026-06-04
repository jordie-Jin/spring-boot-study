package com.sesac.ai.backend.service;

import com.sesac.ai.backend.domain.Clothes;
import com.sesac.ai.backend.dto.ClothesRequest;
import com.sesac.ai.backend.dto.ClothesResponse;
import com.sesac.ai.backend.error.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ClothesService {

    private final Map<Long, Clothes> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public List<ClothesResponse> list() {
        return storage.values().stream()
                .map(ClothesResponse::from)
                .toList();
    }

    public ClothesResponse get(Long id) {
        return ClothesResponse.from(findById(id));
    }

    public ClothesResponse create(ClothesRequest req) {
        long id = sequence.getAndIncrement();
        Clothes saved = req.toEntity();
        saved.setId(id);
        storage.put(id, saved);
        return ClothesResponse.from(saved);
    }

    public ClothesResponse update(Long id, ClothesRequest req) {
        Clothes existing = findById(id);
        existing.setName(req.name());
        existing.setCategory(req.category());
        existing.setBrand(req.brand());
        existing.setColor(req.color());
        existing.setSize(req.size());
        existing.setPrice(req.price());
        existing.setStock(req.stock());
        existing.setMaterial(req.material());
        existing.setSeason(req.season());
        existing.setSoldOut(req.soldOut());
        return ClothesResponse.from(existing);
    }

    public void delete(Long id) {
        if (storage.remove(id) == null) {
            throw NotFoundException.of("clothes", id);
        }
    }

    private Clothes findById(Long id) {
        Clothes clothes = storage.get(id);
        if (clothes == null) {
            throw NotFoundException.of("clothes", id);
        }
        return clothes;
    }
}
