package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    User owner;
    Item item;

    Booking booking;
    Booking booking2;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(1L, "userName", "user@test.com"));
        item = itemRepository.save(new Item(1L, "газоавя горелка", "подойдёт для всех видов работ", true, owner, null));
        booking = bookingRepository.save(new Booking(1L, LocalDateTime.of(2023, 12, 12, 13, 48, 48),
                LocalDateTime.of(2023, 12, 22, 13, 48, 48), item, owner, BookingStatus.WAITING));
        booking2 = bookingRepository.save(new Booking(1L, LocalDateTime.of(2023, 12, 12, 13, 48, 48),
                LocalDateTime.of(2023, 12, 23, 13, 48, 48), item, owner, BookingStatus.WAITING));

    }

    @Test
    void findLastItemBookingTest() {
        Optional<Booking> getBooking = bookingRepository.findLastItemBooking(item.getId(), LocalDateTime.now());
        assertNotNull(getBooking);
    }

    @Test
    void findNextItemBookingTest() {
        Optional<Booking> getBooking = bookingRepository.findNextItemBooking(item.getId(), LocalDateTime.now());
        assertNotNull(getBooking);
    }
}
