package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ShortBookingDto lastBooking;

    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
