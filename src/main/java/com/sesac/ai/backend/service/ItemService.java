package com.sesac.ai.backend.service;

import com.sesac.ai.backend.domain.Item;
import com.sesac.ai.backend.dto.request.ItemRequest;
import com.sesac.ai.backend.dto.response.ItemResponse;
import com.sesac.ai.backend.error.NotFoundException;
import com.sesac.ai.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repository;

    public List<ItemResponse> list() {
        return repository.findAll().stream()
                .map(ItemResponse::from)
                .toList();
    }

    public ItemResponse get(Long id) {
        return ItemResponse.from(findItem(id));
    }

    public ItemResponse create(ItemRequest request) {
        Item saved = repository.save(request.toEntity());
        return ItemResponse.from(saved);
    }

    public ItemResponse update(Long id, ItemRequest request) {
        Item item = findItem(id);
        item.setName(request.name());
        item.setPrice(request.price());

        return ItemResponse.from(repository.save(item));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw NotFoundException.of("Item", id);
        }

        repository.deleteById(id);
    }

    private Item findItem(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Item", id));
    }
}
