package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.request.RequestRepository;
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
    private final RequestRepository requestRepository;

    public List<ItemDto> getByUserId(long userId, Pageable pageable) {
        return itemRepository.findAllByOwnerId(userId, pageable)
                .stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.itemToItemDto(item);
                    List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());
                    itemDto.setNextBooking(bookingMapper.bookingToShortBookingDto(getNextBooking(bookings)));
                    itemDto.setLastBooking(bookingMapper.bookingToShortBookingDto(getLastBooking(bookings)));
                    itemDto.setComments(
                            commentRepository.findAllByItemId(item.getId())
                                    .stream()
                                    .map(commentMapper::commentToCommentDto)
                                    .collect(Collectors.toList())
                    );

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text, Pageable pageable) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository
                .findAllByText(text, pageable)
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getById(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item", id));

        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(id);

            item.setNextBooking(bookingMapper.bookingToShortBookingDto(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.bookingToShortBookingDto(getLastBooking(bookings)));
        }

        item.setComments(
                commentRepository.findAllByItemId(item.getId())
                        .stream()
                        .map(commentMapper::commentToCommentDto)
                        .collect(Collectors.toList())
        );

        return itemMapper.itemToItemDto(item);
    }

    public ItemDto create(long userId, CreateItemDto dto) {
        User user = userService.getById(userId);

        Item newItem = itemMapper.createItemDtoToItem(dto);
        newItem.setOwner(user);

        if (dto.getRequestId() != null) {
            requestRepository.findById(dto.getRequestId()).ifPresentOrElse(newItem::setRequest, () -> {
                throw new NotFoundException("request", dto.getRequestId());
            });
        }

        return itemMapper.itemToItemDto(itemRepository.save(newItem));
    }

    public ItemDto update(long id, long userId, UpdateItemDto dto) {
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

        return itemMapper.itemToItemDto(itemRepository.save(item));
    }

    public ItemDto delete(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("item", id));
        itemRepository.deleteById(id);

        return itemMapper.itemToItemDto(item);
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

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), null);

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
