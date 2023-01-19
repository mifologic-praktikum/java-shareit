package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceImplTest {

    private BookigService bookigService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    BookingMapper bookingMapper;

    Booking booking;
    BookingDto bookingDto;
    NewBookingDto newBookingDto;
    User owner;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        bookigService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);

        owner = new User(1L, "userName", "user@test.com");
        user = new User(2L, "userNameSecond", "user2@test.com");
        item = new Item(1L, "газоавя горелка", "подойдёт для всех видов работ", true, owner, null);
        booking = new Booking(1L, LocalDateTime.of(2023, 12, 12, 13, 48, 48),
                LocalDateTime.of(2023, 12, 22, 13, 48, 48), item, owner, BookingStatus.WAITING);
        bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 12, 13, 48, 48),
                LocalDateTime.of(2023, 12, 22, 13, 48, 48),
                new BookingDto.Item(item.getId(), item.getName()), new BookingDto.User(owner.getId()),
                BookingStatus.APPROVED);
        newBookingDto = new NewBookingDto(1L, LocalDateTime.of(2023, 12, 12, 13, 48, 48),
                LocalDateTime.of(2023, 12, 22, 13, 48, 48), 1L);
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingMapper.toBooking(any(), any(), any()))
                .thenReturn(booking);
        bookigService.createBooking(2L, newBookingDto);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBookingByOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingMapper.toBooking(any(), any(), any()))
                .thenThrow(new NotFoundException("User can't book his own item"));
        assertThrows(RuntimeException.class, () -> bookigService.createBooking(1L, newBookingDto));
    }


    @Test
    void findBookingByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.toBookingDto(any()))
                .thenReturn(bookingDto);
        BookingDto findBooking = bookigService.findBookingById(1L, 1L);
        assertNotNull(findBooking);
    }

    @Test
    void findBookByIdException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(RuntimeException.class, () -> bookigService.findBookingById(6L, booking.getId()));
    }

    @Test
    void findBookingsByOwnerAllStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdOrderByIdDesc(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        bookigService.findBookingsByOwner(1L, BookingState.ALL, 0, 10);
        verify(bookingRepository, times(1)).findAllByItem_Owner_IdOrderByIdDesc(1L, PageRequest.of(0, 10));
    }

    @Test
    void findBookingsByOwnerCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(anyLong(), any(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.CURRENT, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByOwnerFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.FUTURE, 0, 10);
        assertNotNull(bookingDtos);
           }

    @Test
    void findBookingsByOwnerPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndEndBefore(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.PAST, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByOwnerWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.WAITING, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByOwnerRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.REJECTED, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingByUserWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingByUser(1L, BookingState.WAITING, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingByUserUnknownState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        assertThrows(IllegalArgumentException.class, () -> bookigService.findBookingByUser(1L, BookingState.valueOf("UNKNOWN"), 0, 10));

    }

    @Test
    void updateBookingApprovedTTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.toBookingDto(any()))
                .thenReturn(bookingDto);
        bookigService.updateBooking(1L, booking.getId(), true);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingOwnerExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(RuntimeException.class, ()
                -> bookigService.updateBooking(2L, booking.getId(), true));
        verify(bookingRepository, times(0)).save(booking);
    }

    @Test
    void updateBookingStatusExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(RuntimeException.class, ()
                -> bookigService.updateBooking(1L, booking.getId(), true));
        verify(bookingRepository, times(0)).save(booking);
    }
}
