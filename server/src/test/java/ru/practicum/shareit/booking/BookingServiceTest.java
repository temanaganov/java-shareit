package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.core.exception.FieldValidationException;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdOrderByStartDesc() {
        bookingService.getAllByBooker(1, BookingState.ALL, null);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getAllByBooker(1, BookingState.CURRENT, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getAllByBooker(1, BookingState.PAST, null);
        verify(bookingRepository)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        bookingService.getAllByBooker(1, BookingState.FUTURE, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getAllByBooker(1, BookingState.WAITING, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getAllByBooker(1, BookingState.REJECTED, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdOrderByStartDesc() {
        bookingService.getAllByOwner(1, BookingState.ALL, null);
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getAllByOwner(1, BookingState.CURRENT, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getAllByOwner(1, BookingState.PAST, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        bookingService.getAllByOwner(1, BookingState.FUTURE, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getAllByOwner(1, BookingState.WAITING, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getAllByOwner(1, BookingState.REJECTED, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotExists() {
        long userId = 1;
        long itemId = 1;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getById(userId)).thenThrow(new NotFoundException("user", userId));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfItemIsNotExists() {
        long userId = 1;
        long itemId = 1;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfItemIsUnavailable() {
        long userId = 1;
        long itemId = 1;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());
        Item item = TestUtils.makeItem(itemId, false, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotOwner() {
        long userId = 1;
        long itemId = 1;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfDateIsIncorrect() {
        long userId = 1;
        long itemId = 1;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().minusDays(1));
        User user = TestUtils.makeUser(2);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void create_shouldCreateBooking() {
        long userId = 1;
        long itemId = 1;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        User user = TestUtils.makeUser(2);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Booking booking = bookingService.create(userId, dto);

        assertThat(booking).hasFieldOrProperty("id");
    }

    @Test
    void update_shouldThrowNotFoundIfBookingIsNotExists() {
        long bookingId = 1;
        long userId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.update(bookingId, userId, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundIfUserIsNotOwner() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookingId, 2, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowFieldValidationExceptionIfBookingIsAlreadyApproved() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookingId, userId, true)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void update_shouldUpdateBookingToApproved() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        booking = bookingService.update(bookingId, userId, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfBookingIsNotExists() {
        long bookingId = 1;
        long userId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(bookingId, userId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfUserIsNotOwner() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;

        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getById(bookingId, 2)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnBooking() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;

        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(bookingService.getById(bookingId, userId)).isEqualTo(booking);
    }
}
