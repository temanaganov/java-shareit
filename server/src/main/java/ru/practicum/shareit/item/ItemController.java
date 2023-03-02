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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.core.pagination.PaginationMapper;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByUserId(
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return itemService.getByUserId(userId, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return itemService.search(text, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id, @RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemService.getById(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(name = USER_ID_HEADER) long userId, @RequestBody CreateItemDto dto) {
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @PathVariable long id,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @RequestBody UpdateItemDto dto
    ) {
        return itemService.update(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@PathVariable long id) {
        return itemService.delete(id);
    }

    @PostMapping("/{id}/comment")
    public CommentDto comment(
            @PathVariable long id,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @RequestBody CreateCommentDto dto
    ) {
        return itemService.comment(id, userId, dto);
    }
}
