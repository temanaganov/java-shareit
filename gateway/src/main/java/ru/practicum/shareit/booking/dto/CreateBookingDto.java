package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingDto {
	@NotNull(message = "ItemId is required")
	Long itemId;

	@NotNull(message = "Start is required")
	@FutureOrPresent(message = "Start must be in future or present")
	LocalDateTime start;

	@NotNull(message = "End is required")
	@Future(message = "End must be in future")
	LocalDateTime end;
}
