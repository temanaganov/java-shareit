package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<Item> getByUserId(@RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(required = false) String text) {
        return itemService.search(text);
    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable long id) {
        return itemService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader(name = USER_ID_HEADER) long userId, @Valid @RequestBody CreateItemDto dto) {
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public Item update(
            @PathVariable long id,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Valid @RequestBody UpdateItemDto dto
    ) {
        return itemService.update(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public Item delete(@PathVariable long id) {
        return itemService.delete(id);
    }
}
