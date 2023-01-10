package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getByUserId(long userId);
    List<Item> getByText(String text);
    Optional<Item> getById(long id);
    Item create(Item item);
    Item update(Item item);
    Optional<Item> delete(long id);
}
