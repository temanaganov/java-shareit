package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    private ItemService itemService;

    @Test
    void search_shouldReturnEmptyListIfTextIsBlank() {
        assertThat(itemService.search("", null)).isEmpty();
    }

    @Test
    void search_shouldReturnListOfItems() {
        List<Item> items = List.of(
                TestUtils.makeItem(1, true, null),
                TestUtils.makeItem(2, true, null),
                TestUtils.makeItem(3, true, null)
        );
        List<ItemDto> itemDtos = items
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());

        when(itemRepository.findAllByText(anyString(), any())).thenReturn(items);
        assertThat(itemService.search("text", null)).isEqualTo(itemDtos);
    }

    @Test
    void create_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        when(userService.getById(userId)).thenThrow(NotFoundException.class);
        assertThatThrownBy(() -> itemService.create(userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldCreateItemWithRequest() {
        long userId = 1;
        long requestId = 1;
        User user = TestUtils.makeUser(userId);
        Request request = TestUtils.makeRequest(requestId, LocalDateTime.now(), user);
        CreateItemDto createItemDto = TestUtils.makeCreateItemDto(true, requestId);

        when(userService.getById(userId)).thenReturn(user);
        when(requestRepository.findById(userId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.create(userId, createItemDto);

        assertThat(itemDto.getRequestId()).isEqualTo(requestId);
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getOwner()).isEqualTo(user);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long itemId = 1;
        long userId = 1;

        when(userService.getById(userId)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> itemService.update(itemId, userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfRequestIsNotExists() {
        long itemId = 1;
        long userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(itemId, userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateItemName() {
        long itemId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto("new name", null, null);

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getName()).isEqualTo("new name");
    }

    @Test
    void update_shouldUpdateItemDescription() {
        long itemId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto(null, "new description", null);

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getDescription()).isEqualTo("new description");
    }

    @Test
    void update_shouldUpdateItemAvailable() {
        long itemId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, false);

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getAvailable()).isFalse();
    }

    @Test
    void comment_shouldCommentRequest() {
        long itemId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        CreateCommentDto createCommentDto = new CreateCommentDto("new comment");

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(),
                any(),
                any()
        )).thenReturn(List.of(new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED)));
        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        CommentDto commentDto = itemService.comment(itemId, userId, createCommentDto);

        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void delete_shouldDeleteItemAndReturnDeletedItem() {
        long itemId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThat(itemService.delete(itemId)).isEqualTo(itemMapper.itemToItemDto(item));
    }

    @Test
    void getById_should() {
        long itemId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdOrderByStartAsc(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        assertThat(itemService.getById(itemId, userId)).isEqualTo(itemMapper.itemToItemDto(item));
    }
}
