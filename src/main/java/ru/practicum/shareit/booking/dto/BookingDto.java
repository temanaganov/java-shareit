package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingDto {
    @NotNull(message = "ItemId is required")
    Long itemId;

    @NotNull(message = "Start is required")
    LocalDateTime start;

    @NotNull(message = "End is required")
    LocalDateTime end;
}
