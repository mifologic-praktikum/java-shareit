package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookigService {

    BookingDto createBooking(Long userId, NewBookingDto newBookingDto);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto findBookingById(Long userId, Long bookingId);

    List<BookingDto> findBookingsByUser(Long userId, BookingState state, int from, int size);

    List<BookingDto> findBookingsByOwner(Long userId, BookingState state, int from, int size);
}
