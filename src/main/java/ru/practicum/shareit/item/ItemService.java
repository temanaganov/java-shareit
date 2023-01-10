package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public List<Item> getAll(long userId) {
        return itemRepository.getByUserId(userId);
    }

    public List<Item> search(String text) {
        return itemRepository.getByText(text);
    }

    public Item getById(long id) {
        return itemRepository.getById(id).orElseThrow(() -> new NotFoundException("item", id));
    }

    public Item create(long userId, CreateItemDto dto) {
        userService.getById(userId);
        Item newItem = Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(userId)
                .build();

        return itemRepository.create(newItem);
    }

    public Item update(long id, long userId, UpdateItemDto dto) {
        userService.getById(userId);
        Item item = itemRepository.getById(id).orElseThrow(() -> new NotFoundException("item", id));

        if (userId != item.getOwner()) {
            throw new NotFoundException("item", id);
        }

        if (dto.getName() != null) {
            item = item.withName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item = item.withDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item = item.withAvailable(dto.getAvailable());
        }

        return itemRepository.update(item);
    }

    public Item delete(long id) {
        return itemRepository.delete(id).orElseThrow(() -> new NotFoundException("item", id));
    }
}
