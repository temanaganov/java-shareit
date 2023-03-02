package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.core.exception.FieldValidationException;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public List<Booking> getAllByBooker(long bookerId, BookingState state, Pageable pageable) {
        userService.getById(bookerId);

        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageable
                );
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        pageable
                );
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        pageable
                );
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId,
                        BookingStatus.WAITING,
                        pageable
                );
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId,
                        BookingStatus.REJECTED,
                        pageable
                );
            default:
                throw new UnsupportedStatusException();
        }
    }

    public List<Booking> getAllByOwner(long ownerId, BookingState state, Pageable pageable) {
        userService.getById(ownerId);

        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageable
                );
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        ownerId,
                        LocalDateTime.now(),
                        pageable
                );
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId,
                        LocalDateTime.now(),
                        pageable
                );
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId,
                        BookingStatus.WAITING,
                        pageable
                );
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId,
                        BookingStatus.REJECTED,
                        pageable
                );
            default:
                throw new UnsupportedStatusException();
        }
    }

    public Booking getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking", bookingId));

        boolean isOwner = booking.getItem().getOwner().getId() == userId;
        boolean isBooker = booking.getBooker().getId() == userId;

        if (!(isOwner || isBooker)) {
            throw new NotFoundException("booking", bookingId);
        }

        return booking;
    }

    public Booking create(long userId, CreateBookingDto dto) {
        User booker = userService.getById(userId);
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("item", dto.getItemId()));

        boolean isItemUnavailable = !item.getAvailable();

        if (isItemUnavailable) {
            throw new FieldValidationException("itemId", "Item with this id is unavailable");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("booking", item.getId());
        }

        Booking booking = bookingMapper.createBookingDtoToBooking(dto);

        boolean isStartInPast = booking.getStart().isBefore(LocalDateTime.now());
        boolean isEndInPast = booking.getEnd().isBefore(LocalDateTime.now());
        boolean isEndBeforeStart = booking.getEnd().isBefore(booking.getStart());

        if (isStartInPast || isEndInPast || isEndBeforeStart) {
            throw new FieldValidationException("start | end", "Time is incorrect");
        }

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        return bookingRepository.save(booking);
    }

    public Booking update(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking", bookingId));

        boolean isOwner = booking.getItem().getOwner().getId() == ownerId;

        if (!isOwner) {
            throw new NotFoundException("booking", bookingId);
        }

        boolean isBookingApprovedOrRejected = booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED;

        if (isBookingApprovedOrRejected) {
            throw new FieldValidationException("bookingId", "Booking is already approved or rejected");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        return bookingRepository.save(booking);
    }
}
