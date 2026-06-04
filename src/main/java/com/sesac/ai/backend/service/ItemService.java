package com.sesac.ai.backend.service;

import com.sesac.ai.backend.domain.Item;
import com.sesac.ai.backend.dto.ItemRequest;
import com.sesac.ai.backend.dto.ItemResponse;
import com.sesac.ai.backend.error.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public List<ItemResponse> list() {
        return storage.values().stream()
                .map(ItemResponse::from)
                .toList();
    }

    public ItemResponse get(Long id) {
        return ItemResponse.from(findById(id));
    }

    public ItemResponse create(ItemRequest req) {
        long id = sequence.getAndIncrement();
        Item saved = req.toEntity();
        saved.setId(id);
        storage.put(id, saved);
        return ItemResponse.from(saved);
    }

    public ItemResponse update(Long id, ItemRequest req) {
        Item existing = findById(id);
        existing.setName(req.name());
        existing.setPrice(req.price());
        return ItemResponse.from(existing);
    }

    public void delete(Long id) {
        if (storage.remove(id) == null) {
            throw NotFoundException.of("item", id);
        }
    }

    private Item findById(Long id) {
        Item item = storage.get(id);
        if (item == null) {
            throw NotFoundException.of("item", id);
        }
        return item;
    }
}
