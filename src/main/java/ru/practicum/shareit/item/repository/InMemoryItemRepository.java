package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public List<Item> getByUserId(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return items.values()
                .stream()
                .filter(item -> item.getAvailable() && (
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        long id = getNextId();
        item.setId(id);
        items.put(id, item);

        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Optional<Item> delete(long id) {
        return Optional.ofNullable(items.remove(id));
    }

    private long getNextId() {
        return ++id;
    }
}
