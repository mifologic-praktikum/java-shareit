package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                new BookingDto.User(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public static ItemBookingDto toItemBookingDto(Booking booking) {
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    public static Booking toBooking(NewBookingDto newBookingDto, Item item, User user) {
        return new Booking(
                newBookingDto.getId(),
                newBookingDto.getStart(),
                newBookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public static List<BookingDto> toListBookingDto(List<Booking> bookingList) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtos.add(toBookingDto(booking));
        }
        return bookingDtos;
    }
}
