package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class BookingMapperTest {
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void bookingToShortBookingDto() {
        User user = TestUtils.makeUser(1);
        Item item = TestUtils.makeItem(1, true, user);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        ShortBookingDto dto = bookingMapper.bookingToShortBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(1L);
    }
}
