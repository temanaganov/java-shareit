package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.core.exception.FieldValidationException;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public List<Item> getByUserId(long userId) {
        return itemRepository.findAllByOwner_Id(userId)
                .stream()
                .peek(item -> {
                    List<Booking> bookings = bookingRepository.findAllByItem_IdOrderByStartAsc(item.getId());
                    item.setNextBooking(bookingMapper.bookingToShortBookingDto(getNextBooking(bookings)));
                    item.setLastBooking(bookingMapper.bookingToShortBookingDto(getLastBooking(bookings)));
                    item.setComments(
                            commentRepository.findAllByItem_Id(item.getId())
                                    .stream()
                                    .map(commentMapper::commentToCommentDto)
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findAllByText(text);
    }

    public Item getById(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item", id));

        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findAllByItem_IdOrderByStartAsc(id);

            item.setNextBooking(bookingMapper.bookingToShortBookingDto(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.bookingToShortBookingDto(getLastBooking(bookings)));
        }

        item.setComments(
                commentRepository.findAllByItem_Id(item.getId())
                        .stream()
                        .map(commentMapper::commentToCommentDto)
                        .collect(Collectors.toList())
        );

        return item;
    }

    public Item create(long userId, CreateItemDto dto) {
        User user = userService.getById(userId);
        Item newItem = itemMapper.createItemDtoToItem(dto);
        newItem.setOwner(user);

        return itemRepository.save(newItem);
    }

    public Item update(long id, long userId, UpdateItemDto dto) {
        User user = userService.getById(userId);
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item", id));

        if (!user.equals(item.getOwner())) {
            throw new NotFoundException("item", id);
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return itemRepository.save(item);
    }

    public Item delete(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item", id));
        itemRepository.deleteById(id);
        return item;
    }

    private Booking getNextBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(0);
    }

    private Booking getLastBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(filteredBookings.size() - 1);
    }

    public CommentDto comment(long id, long userId, CreateCommentDto commentDto) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item", id));
        User user = userService.getById(userId);

        List<Booking> bookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new FieldValidationException("userId", "User didn't book this item");
        }

        Comment comment = commentMapper.createCommentDtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }
}
