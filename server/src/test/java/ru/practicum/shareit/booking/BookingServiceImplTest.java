package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

    private BookigService bookigService;

    private ItemRepository itemRepository;

    private UserRepository userRepository;

    private BookingRepository bookingRepository;

    Booking booking;
    BookingDto bookingDto;
    NewBookingDto newBookingDto;
    User owner;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookigService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
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
        BookingDto bookingDtoCreated = bookigService.createBooking(2L, newBookingDto);
        assertNotNull(bookingDtoCreated);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBookingByOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(RuntimeException.class, () -> bookigService.createBooking(1L, newBookingDto));
    }

    @Test
    void createBookingUnavailableITest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        item.setAvailable(false);
        assertThrows(BadRequestException.class, () ->
                bookigService.createBooking(2L, newBookingDto));
    }

    @Test
    void createBookingUserCantBookTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () ->
                bookigService.createBooking(1L, newBookingDto));
    }


    @Test
    void createBookingUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> bookigService.createBooking(42L, newBookingDto));
    }

    @Test
    void createBookingItemNotFoundTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> bookigService.createBooking(1L, newBookingDto));
    }

    @Test
    void findBookingByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
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
    void findBookingByIdBookingNotFoundTest() {
        assertThrows(NotFoundException.class, () -> bookigService.findBookingById(1L, 42L));
    }

    @Test
    void findBookingsByOwnerAllStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdOrderByIdDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));
        bookigService.findBookingsByOwner(1L, BookingState.ALL, 0, 10);
        verify(bookingRepository, times(1)).findAllByItem_Owner_IdOrderByIdDesc(anyLong(), any());
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
        when(bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.FUTURE, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByOwnerPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndEndBefore(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.PAST, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByOwnerWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.WAITING, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByOwnerRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByOwner(1L, BookingState.REJECTED, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingByOwnerUnknownState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> bookigService.findBookingsByOwner(1L, BookingState.valueOf("UNKNOWN"), 0, 10));
    }

    @Test
    void findBookingsByOwnerNegativeTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> bookigService.findBookingsByOwner(1L, BookingState.WAITING, -2, 10));
    }

    @Test
    void findBookingsByOwnerUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> bookigService.findBookingsByOwner(42L, BookingState.WAITING, 0, 10));
    }

    @Test
    void findBookingsByUserAllStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByIdDesc(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        bookigService.findBookingsByUser(2L, BookingState.ALL, 0, 10);
        verify(bookingRepository, times(1)).findAllByBooker_IdOrderByIdDesc(2L, PageRequest.of(0, 10));
    }

    @Test
    void findBookingsByUserCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(anyLong(), any(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByUser(2L, BookingState.CURRENT, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByUserFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByUser(2L, BookingState.FUTURE, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByUserPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndEndBefore(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByUser(2L, BookingState.PAST, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByUserWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByUser(1L, BookingState.WAITING, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByUserRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), PageRequest.of(anyInt(), 10)))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtos = bookigService.findBookingsByUser(2L, BookingState.REJECTED, 0, 10);
        assertNotNull(bookingDtos);
    }

    @Test
    void findBookingsByUserUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> bookigService.findBookingsByUser(2L, BookingState.valueOf("UNKNOWN"), 0, 10));
    }

    @Test
    void findBookingsByUserUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> bookigService.findBookingsByUser(42L, BookingState.REJECTED, 0, 10));
    }

    @Test
    void findBookingsByUserNegativeTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> bookigService.findBookingsByUser(1L, BookingState.WAITING, -2, 10));
    }

    @Test
    void updateBookingApprovedTTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        bookigService.updateBooking(1L, booking.getId(), true);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateBookingRejectedTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        bookigService.updateBooking(1L, booking.getId(), false);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateBookingOwnerExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(RuntimeException.class, ()
                -> bookigService.updateBooking(2L, booking.getId(), true));
    }

    @Test
    void updateBookingStatusExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(BadRequestException.class, ()
                -> bookigService.updateBooking(1L, booking.getId(), true));
    }

    @Test
    void updateBookingBookingNotFoundTest() {
        assertThrows(NotFoundException.class, ()
                -> bookigService.updateBooking(1L, 42L, true));
    }
}
