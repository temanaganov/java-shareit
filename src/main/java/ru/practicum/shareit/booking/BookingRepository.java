package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime start);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    List<Booking> findAllByItem_IdOrderByStartAsc(long itemId);
}
