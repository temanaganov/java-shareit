package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotNull(message = "ItemId is required")
    Long itemId;

    @NotNull(message = "Start is required")
    LocalDateTime start;

    @NotNull(message = "End is required")
    LocalDateTime end;
}
