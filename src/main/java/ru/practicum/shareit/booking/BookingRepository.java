package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByIdDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndEndBefore(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdOrderByIdDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemAndBookerIdAndStatusAndEndBefore(Item item, Long bookerId, BookingStatus bookingStatus, LocalDateTime date);

    @Query(value = "SELECT * FROM BOOKINGS b INNER JOIN ITEMS i on i.ID = b.ITEM_ID "
            + "WHERE b.ITEM_ID = ?1 AND b.END_DATE < ?2 ORDER BY b.END_DATE ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> findLastItemBooking(Long itemId, LocalDateTime nowTime);

    @Query(value = "SELECT * FROM BOOKINGS b INNER JOIN ITEMS i on i.ID = b.ITEM_ID "
            + "WHERE b.ITEM_ID = ?1 and b.START_DATE > ?2 ORDER BY b.START_DATE ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> findNextItemBooking(Long itemId, LocalDateTime nowTime);

}
