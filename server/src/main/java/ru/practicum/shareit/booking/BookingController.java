package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.core.pagination.PaginationMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping
    public List<Booking> getAllByBooker(
            @RequestHeader(USER_ID_HEADER) long bookerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return bookingService.getAllByBooker(bookerId, state, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/owner")
    public List<Booking> getAllByOwner(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        return bookingService.getAllByOwner(ownerId, state, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestHeader(USER_ID_HEADER) long userId, @RequestBody CreateBookingDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public Booking update(
            @PathVariable long bookingId,
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam boolean approved
    ) {
        return bookingService.update(bookingId, ownerId, approved);
    }
}
