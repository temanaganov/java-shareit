package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping
    public List<Booking> getAllByBooker(
            @RequestHeader(name = USER_ID_HEADER) long bookerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllByOwner(
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByOwner(ownerId, state);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable long bookingId, @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @PostMapping
    public Booking create(@RequestHeader(name = USER_ID_HEADER) long userId, @Valid @RequestBody BookingDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public Booking update(
            @PathVariable long bookingId,
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @RequestParam boolean approved
    ) {
        return bookingService.update(bookingId, ownerId, approved);
    }
}
